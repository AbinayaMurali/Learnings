from flask import Flask, Response, request
from flask_restful import Api
from smtplib import SMTP
from email.mime.multipart import MIMEMultipart

app = Flask(__name__)
api = Api(app)


@app.route('/email', methods=['POST'])
def create_email():
    if not request.json \
            or 'from' not in request.json \
            or 'subject' not in request.json \
            or 'to' not in request.json \
            or 'body' not in request.json:
        return 'Required request body is missing'

    msg = MIMEMultipart()
    body = request.json['body']
    me = request.json['from']
    you = request.json['to']
    msg['Subject'] = request.json['subject']
    msg['From'] = me
    msg['To'] = str(you)

    try:
        server_ssl = SMTP(<<Exchange relay value>>)
        # send the message via the server.
        server_ssl.sendmail(me, you, body)
    except Exception as e:
        print('Exception occurred : '+str(e))
    finally:
        server_ssl.quit()

    return "Email sent"


if __name__ == '__main__':
    app.run(debug=True)
