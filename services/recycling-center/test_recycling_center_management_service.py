import unittest
from flask import Flask
from recycling_center_management_service import get_recycling_centers

class TestRecyclingCenterManagementService(unittest.TestCase):
    def setUp(self):
        self.app = Flask(__name__)
        self.app.config['TESTING'] = True

    def test_get_recycling_centers(self):
        with self.app.test_request_context('/recycling_centers', method='GET'):
            response = get_recycling_centers()
            self.assertEqual(response.status_code, 200)
            self.assertEqual(response.get_json(), [])

if __name__ == '__main__':
    unittest.main()