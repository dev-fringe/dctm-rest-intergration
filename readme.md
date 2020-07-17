13:38:44.696 [main] INFO  Response - request method: POST, 
request URI: http://127.0.0.1:8080/dctm-rest/repositories/bla/folders/0b00271180028308/documents?require-dc-write=true&media-url-policy=dc-pref&format=crtext&content-length=23, request headers: {Accept=[application/vnd.emc.documentum+json], Content-Type=[application/vnd.emc.documentum+json], Authorization=[Basic ZG1hZG1pbjphZG1pbiEyMzQ=], Content-Length=[74]}
, request body: {"properties":{"object_type":"h_bla","object_name":"test_object_bla"}}
, response status code: 400 BAD_REQUEST, response headers: {Vary=[Accept-Encoding], X-Content-Type-Options=[nosniff], X-XSS-Protection=[1; mode=block], Cache-Control=[no-cache, no-store, max-age=0, must-revalidate], Pragma=[no-cache], Expires=[0], X-Frame-Options=[DENY], Content-Type=[application/vnd.emc.documentum+json;charset=UTF-8], Transfer-Encoding=[chunked], Date=[Thu, 16 Jul 2020 04:38:44 GMT], Connection=[close]}
, response body: {"status":400,"code":"E_INVALID_ATTR_NAME","message":"The operation failed because an invalid attribute name is provided.","details":"(DM_API_E_BADATTRNAME) [DM_API_E_BADATTRNAME]error:  \"Bad attribute name 'object_type' for document/object.\"","id":"2182c6f7-9da6-40d0-b561-967efbd90fea"}
  [error] status [400]
  [error] code [E_INVALID_ATTR_NAME]
  [error] message [The operation failed because an invalid attribute name is provided.]
  [error] detail [(DM_API_E_BADATTRNAME) [DM_API_E_BADATTRNAME]error:  "Bad attribute name 'object_type' for document/object."]
  [error] id [2182c6f7-9da6-40d0-b561-967efbd90fea]
