import requests

response = requests.{{request-method|name}}(
    '{{scheme}}://{{server-name}}{{uri}}',
    {% if query-string %}params='{{query-string|safe}}',{% endif %}
    {% if headers %}headers={{headers|json|safe|default:"{}"}},{% endif %}
    {% if body %}data=({{body|json|safe}}){% endif %}
)

# Inspect some attributes of the `requests` repository
json_response = response.json()
print(json_response);
