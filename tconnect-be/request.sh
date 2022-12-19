curl http://localhost:8080/user \
-u 'customer1:customer1'


curl http://localhost:8080/projects \
-u 'customer1:customer1' \
-H 'Content-Type: application/json; charset=utf-8' \
--data-binary @- << EOF
{
   "title": "The grand project",
   "description": "The project is to construct a patio in the\nbackyard, and a fire place.\n",
   "expectedHours": 55,
   "biddingEndTime": "2022-12-19T10:32:58.335399Z"
}
EOF