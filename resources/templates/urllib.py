import urllib.parse
import urllib.request

uri = {{scheme}}://{{server-name}}{{uri}}
headers = {{headers|json|safe}}
data = {{body|json|safe}}
req = urllib.request.Request(url, data, headers)

with urllib.request.urlOpen(req) as response:
   resp = response.read()
   print resp

# TODO Not tested! It's just a placeholder
# https://docs.python.org/3/howto/urllib2.html