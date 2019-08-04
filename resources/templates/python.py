import requests

{% if headers %}headers={{headers|safe}},{% endif %}
{% if body %}json={{body|safe}}{% endif %}

response = requests.{{request-method|name}}(
    '{{scheme}}://{{server-name}}{{uri}}',
    {% if query-string %}params='{{query-string|safe}}',{% endif %}
    {% if headers %}headers=(headers),{% endif %}
    {% if body %}json=(json){% endif %}
)

print({
    {% ifequal request-method :delete %}
    'raw_body': response.text,
    {% else %}
    'raw_body': response.json(),
    {% endifequal %}
    'status': response.status_code,
    'code': response.status_code
})
