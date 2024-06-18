import unittest
import requests
import json
import logging
import socket
import os
import pytest

# Global variables and functions
scores = {}
logs_directory = '/home/ubuntu/'

def check_server(address, port):
    # Create a TCP socket
    s = socket.socket()
    try:
        s.connect((address, port))
        return True
    except socket.error:
        return False
    finally:
        s.close()

def clean_db():
    os.system('mongo Xmeme --eval "db.dropDatabase()"')

class XMemeAssessment(unittest.TestCase):

    HEADERS = None

    def __init__(self, *args, **kwargs):
        super(XMemeAssessment, self).__init__(*args, **kwargs)
        self.HEADERS = {"Content-Type": "application/json"}
        self.localhost = 'http://localhost:8081/'
        self.SAMPLE_URL = 'https://cwod-assessment-images.s3.ap-south-1.amazonaws.com/images/'
        self.POSITIVE_STATUS_CODES = [200, 201, 202, 203]
        self.NEGATIVE_STATUS_CODES = [400, 401, 402, 403, 404, 405, 409]

    def get_api(self, endpoint):
        response = requests.get(self.localhost + endpoint, headers=self.HEADERS)
        self.print_curl_request_and_response(response)
        return response

    def post_api(self, endpoint, body):
        response = requests.post(self.localhost + endpoint, headers=self.HEADERS, data=body)
        self.print_curl_request_and_response(response)
        return response

    def print_curl_request_and_response(self, response):
        if response.status_code in self.POSITIVE_STATUS_CODES:
            self.decode_and_load_json(response)

    def patch_api(self, endpoint, body):
        response = requests.patch(self.localhost + endpoint, headers=self.HEADERS, data=body)
        self.print_curl_request_and_response(response)
        return response

    def decode_and_load_json(self, response):
    try:
        return response.json()
    except ValueError:
        self.fail("Invalid JSON response")


    @pytest.fixture(scope="session", autouse=True)
    def db_cleanup(self, request):
        clean_db()
        request.addfinalizer(clean_db)

    @pytest.mark.run(order=1)
    def test_0_get_on_empty_db_test(self):
        endpoint = 'memes/'
        response = self.get_api(endpoint)
        self.assertEqual(response.status_code, 200)
        data = self.decode_and_load_json(response)
        self.assertEqual(len(data), 0)

    @pytest.mark.run(order=2)
    def test_1_first_post_test(self):
    """Post first MEME and verify that it returns id in the response"""
    endpoint = 'memes/'
    body = {
        'name': 'crio-user',
        'caption': 'crio-meme',
        'url': self.SAMPLE_URL + self.FIRST_POST
    }
    response = self.post_api(endpoint, json.dumps(body))
    self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)
    data = self.decode_and_load_json(response)
    self.FIRST_POST_ID = data['id']


    @pytest.mark.run(order=3)
    def test_2_get_single_meme(self):  # Score 6
    """Post a new MEME, capture its Id, and verify its GET /meme/{id} returns correct MEME"""
    endpoint = 'memes/'
    body = {
        'name': 'crio-user' + "9999",
        'caption': 'crio-meme' + "9999",
        'url': self.SAMPLE_URL + self.FIRST_POST
    }
    response = self.post_api(endpoint, json.dumps(body))
    self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)
    data = self.decode_and_load_json(response)
    endpoint = f'memes/{data["id"]}'
    get_response = self.get_api(endpoint)
    self.assertIn(get_response.status_code, self.POSITIVE_STATUS_CODES)
    get_data = self.decode_and_load_json(get_response)
    self.assertEqual(get_data['name'], body['name'])
    self.assertEqual(get_data['caption'], body['caption'])
    self.assertEqual(get_data['url'], body['url'])

    
    @pytest.mark.run(order=4)
    def test_3_get_single_meme_non_existent_test(self):
        endpoint = 'memes/0909'
        response = self.get_api(endpoint)
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=5)
    def test_4_post_duplicate_test(self):
        endpoint = 'memes/'
        body = json.dumps({
            'name': 'crio-user',
            'caption': 'crio-meme',
            'url': self.SAMPLE_URL + '130.png'
        })
        response = self.post_api(endpoint, body)
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=6)
    def test_5_post_empty_test(self):
        endpoint = 'memes/'
        body = json.dumps({})
        response = self.post_api(endpoint, body)
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=7)
    def test_6_less_than_100_post_test(self):
        endpoint = 'memes/'
        for i in range(1, 50):
            body = json.dumps({
                'name': f'crio-user-{i}',
                'caption': f'crio-meme-{i}',
                'url': self.SAMPLE_URL + f'{i}.png'
            })
            response = self.post_api(endpoint, body)
            self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)

        get_response = self.get_api(endpoint)
        data = self.decode_and_load_json(get_response)
        self.assertGreater(len(data), 50)

    @pytest.mark.run(order=8)
    def test_7_more_than_100_post_test(self):
        endpoint = 'memes/'
        for i in range(51, 104):
            body = json.dumps({
                'name': f'A{i}',
                'caption': f'B{i}',
                'url': self.SAMPLE_URL + f'{i}.png'
            })
            response = self.post_api(endpoint, body)
        self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)

        new_response = self.get_api(endpoint)
        self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)

        data = self.decode_and_load_json(new_response)
        self.assertEqual(len(data), 100)
        self.assertEqual(data[99]["name"], 'crio-user-3')
        self.assertEqual(data[0]["name"], 'A103')

if __name__ == '__main__':
    unittest.main()

