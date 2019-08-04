```python
import requests

response = requests.put(
    'https://api.oftrust.net/messages/{version}/{id}',
    
    headers={"Authorization":"Bearer <ACCESS_TOKEN>","Content-Type":"application/json"},
    json=({"subject":"Go to the grocery store","content":"Remember to buy milk!"})
)

json_response = response.json()
print({'raw_body': json_response, 'status': response.status_code, 'code': response.status_code})

```

```shell
curl -i \
    -X PUT \
    -H "Authorization: Bearer <ACCESS_TOKEN>" \
    -H "Content-Type: application/json" \
    --data-binary "{\"subject\":\"Go to the grocery store\",\"content\":\"Remember to buy milk!\"}" \
    "https://api.oftrust.net/messages/{version}/{id}"
```

```javascript
const unirest = require("unirest");

unirest
  .put("https://api.oftrust.net/messages/{version}/{id}")
  .headers({"Authorization":"Bearer <ACCESS_TOKEN>","Content-Type":"application/json"})
  
  .send({"subject":"Go to the grocery store","content":"Remember to buy milk!"})
  .then(({raw_body, status, code }) => {
    // output response to console as JSON
    console.log(JSON.stringify({ raw_body, status, code }, null, 4));
  })

```

```java
System.out.println("Java example missing. Why not contribute one for us?");
```

> The above example should return `JSON` structured like this:

```json
The above example should return JSON structured like this:

HTTP/1.0 200

{
  "@context": "<URL to message context>",
  "@type": "Message",
  "toIdentity": "<to identity ID>",
  "subject": "<message subject>",
  "content": "<message content>",
  "cc": [
    "<list of identity IDs>"
  ],
  "createdAt": "2019-01-10T12:00:00Z",
  "updatedAt": "2019-01-10T12:00:00Z",
  "createdBy": "<user ID who created the message>"
}

```
