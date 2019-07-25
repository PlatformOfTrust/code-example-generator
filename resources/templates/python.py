import requests

response = requests.{{request-method|name}}(
    '{{scheme}}://{{server-name}}{{uri}}',
    {% if query-string %}params='{{query-string|safe}}',{% endif %}
    {% if headers %}headers={{headers|json|safe}},{% endif %}
    {% if body %}json=({{body|json|safe}}){% endif %}
)

json_response = response.json()
print({'raw_body': json_response, 'status': response.status_code, 'code': response.status_code})
