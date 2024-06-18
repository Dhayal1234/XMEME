import unittest
from unittest import TestCase

import curlify
import pytest
import requests
import json
import logging
import socket
import os
import uuid

## Global variables and functions
from pytest import fail

scores = {}
logs_directory = '/home/ubuntu/'

def check_server(address, port):
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

class XMemeAssessment(TestCase):

    HEADERS = None

    def __init__(self, *args, **kwargs):
        unittest.TestCase.__init__(self, *args, **kwargs)
        self.HEADERS = {"Content-Type": "application/json"}
        self.localhost = 'http://localhost:8081/'

        self.SAMPLE_URL = 'https://cwod-assessment-images.s3.ap-south-1.amazonaws.com/images/'
        self.FIRST_POST_ID = ''
        self.FIRST_POST = '130.png'

        self.SECOND_POST_ID = ''
        self.SECOND_POST = '132.png'
        self.UPDATED_POST = '133.png'

        self.POSITIVE_STATUS_CODES = [200, 201, 202, 203]
        self.NEGATIVE_STATUS_CODES = [400, 401, 402, 403, 404, 405, 409]

    ### Helper functions
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
            text_response = response.content.decode('utf-8')
            data = json.loads(text_response)
        except Exception as e:
            logging.exception(str(e))
            return response
        return data

    ### Helper functions end here

    @pytest.fixture(scope="session", autouse=True)
    def db_cleanup(self, request):
        clean_db()
        request.addfinalizer(clean_db)

    @pytest.mark.run(order=1)
    def test_0_get_on_empty_db_test(self):
        """When run with empty database, get calls should return success, and response should be empty"""
        clean_db()
        endpoint = 'memes/'
        response_with_slash = self.get_api(endpoint)
        self.assertEqual(response_with_slash.status_code, 200)
        response_length = len(self.decode_and_load_json(response_with_slash))
        self.assertEqual(response_length, 0)

    # First Post
    @pytest.mark.run(order=2)
    def test_1_first_post_test(self):
        """Post first MEME and verify that it returns id in the response"""
        endpoint = 'memes/'
        body = {
            'name': 'crio-user-' + str(uuid.uuid4()),
            'caption': 'crio-meme',
            'url': self.SAMPLE_URL + self.FIRST_POST + str(uuid.uuid4())
        }
        response = self.post_api(endpoint, json.dumps(body))
        self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)
        data = self.decode_and_load_json(response)
        self.FIRST_POST_ID = data['id']

    @pytest.mark.run(order=3)
    def test_2_get_single_meme(self):
        """Post a new MEME, capture its Id, and verify its GET /meme/{id} returns correct MEME"""
        endpoint = 'memes/'
        body = {
            'name': 'crio-user' + str(uuid.uuid4()),
            'caption': 'crio-meme' + str(uuid.uuid4()),
            'url': self.SAMPLE_URL + self.FIRST_POST + str(uuid.uuid4())
        }
        response = self.post_api(endpoint, json.dumps(body))
        self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)
        data = self.decode_and_load_json(response)

        endpoint = 'memes/{}'.format(data["id"])
        response = self.get_api(endpoint)
        self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)
        data = self.decode_and_load_json(response)
        self.assertEqual(data['name'], body['name'])
        self.assertEqual(data['caption'], body['caption'])
        self.assertEqual(data['url'], body['url'])

    @pytest.mark.run(order=4)
    def test_3_get_single_meme_non_existent_test(self):
        """Try to access MEME with some random id, and verify that it returns 404"""
        endpoint = 'memes/0909'
        response = self.get_api(endpoint)
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=5)
    def test_4_post_duplicate_test(self):
        """Verify that posting duplicate MEME return 409"""
        endpoint = 'memes/'
        body = {
            'name': 'crio-user',
            'caption': 'crio-meme',
            'url': self.SAMPLE_URL + self.FIRST_POST
        }
        response = self.post_api(endpoint, json.dumps(body))
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=6)
    def test_5_post_empty_test(self):
        """Verify that API doesn't accept empty data in POST call"""
        endpoint = 'memes/'
        body = {}
        response = self.post_api(endpoint, json.dumps(body))
        self.assertIn(response.status_code, self.NEGATIVE_STATUS_CODES)

    @pytest.mark.run(order=7)
    def test_6_less_than_100_post_test(self):
        """Insert 50 MEMEs and try accessing them to confirm that all of them are returned back"""
        endpoint = 'memes/'
        for i in range(1, 50):
            body = {
                'name': 'crio-user-' + str(uuid.uuid4()),
                'caption': 'crio-meme-' + str(i),
                'url': self.SAMPLE_URL + str(uuid.uuid4()) + '.png'
            }
            response = self.post_api(endpoint, json.dumps(body))
            self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)

        get_response = self.get_api(endpoint)
        data = self.decode_and_load_json(get_response)
        self.assertGreaterEqual(len(data), 50)

    @pytest.mark.run(order=8)
    def test_7_more_than_100_post_test(self):
        """Post more than 100 MEME, make a GET call and ensure that it returns only latest 100 MEME"""
        endpoint = 'memes/'
        for i in range(51, 104):
            body = {
                'name': 'A' + str(uuid.uuid4()),
                'caption': 'B' + str(uuid.uuid4()),
                'url': self.SAMPLE_URL + str(uuid.uuid4()) + '.png'
            }
            response = self.post_api(endpoint, json.dumps(body))
            self.assertIn(response.status_code, self.POSITIVE_STATUS_CODES)

        new_response = self.get_api(endpoint)
        self.assertIn(new_response.status_code, self.POSITIVE_STATUS_CODES)
        data = self.decode_and_load_json(new_response)
        self.assertEqual(len(data), 100)
        self.assertEqual(data[99]["name"], 'crio-user-3')
        self.assertEqual(data[0]["name"], 'A103')

if __name__ == '__main__':
    unittest.main()
