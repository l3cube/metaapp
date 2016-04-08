from flask import Flask
from flask import request, Response
from flask import render_template
from flask import redirect, url_for, flash, session
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import and_,or_
import json
import seller_sdk as seller

seller_server_ip = '10.42.0.1'
seller_server_port = '8787'

app = Flask(__name__)
app.config.from_object(__name__)
app.secret_key = 'some_secret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/MetaAppSellerServer.db'
db = SQLAlchemy(app)


class Credentials(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200))
    password = db.Column(db.String(200))
    client_key = db.Column(db.String(200))
    filters = db.Column(db.String(200))

    def __init__(self, name, password, client_key, filters):
        self.name = name
        self.password = password
        self.client_key = client_key
        self.filters = filters

    def __repr__(self):
        return '<Requests %r>' % self.name


@app.route('/', methods=['GET'])
def homepage():
    return render_template("index.html")


@app.route('/display/', methods=['GET'])
def display():
    data = json.loads(seller.get_requests(session['Filters']))
    print data
    entries = [dict(uuid=row['Uuid'], reqid=row['RequestId'], deadline=row['Deadline'], topic=row['Topic'],
                    intentdesc=row['Intent']) for row in data]
    return render_template("seller-homepage.html", entries=entries, username=session['Name'])


@app.route('/login/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        # Do validation
        curr = Credentials.query.filter(Credentials.name == username).first()
        if curr is not None:
            if curr.password == password:
                session['Name'] = curr.name
                session['Password'] = password
                session['Client_Key'] = curr.client_key
                session['Filters'] = curr.filters
                return redirect(url_for('display'))
            else:
                flash(u'Wrong Password!!!', 'danger')
                return redirect(url_for('login'))
        else:
            flash(u'UnSuccessfull Login!!!', 'danger')
            return redirect(url_for('login'))
    else:
        return render_template("signin.html")


@app.route('/logout/', methods=['POST'])
def logout():
    session.pop('Name', None)
    session.pop('Password', None)
    session.pop('Client_Key', None)
    session.pop('Filters', None)
    return redirect(url_for('homepage'))


@app.route('/register/', methods=['POST', 'GET'])
def register():
    if request.method == 'POST':
        name = request.form['Name']
        password = request.form['Password']
        handle = request.form['Handle']
        endpoint = request.form['Endpoint']
        filters = request.form.getlist('Filter')
        print name
        print handle
        print endpoint
        print filters
        strn = ''
        for i in filters:
            strn = strn + i + ' '
        strn = strn.strip()
        res = seller.register(name, strn, handle, endpoint)
        if res.status_code == 200:
            data = json.loads(res.text)
            client_key = data['Client_Key']
            new_entry = Credentials(name, password, client_key, strn)
            db.session.add(new_entry)
            db.session.commit()
            flash(u'Successfull Registration!!! Client Key: ' + client_key, 'success')
            return redirect(url_for('login'))
        elif res.status_code == 404:
            data = json.loads(res.text)
            error = data['Error']
            flash(u'UnSuccessfull Registration!!! Error: ' + error, 'danger')
            return redirect(url_for('register'))
    else:
        return render_template("register.html")


@app.route('/respond/', methods=['POST'])
def make_reply():
    if request.method == 'POST':
        print request.form
        uuid = request.form['uuid']
        reqid = request.form['requestid']
        topic = request.form['topic']
        servicedesc = request.form['servicedesc']
        cost = request.form['cost']
        ttc = request.form['ttc']
        d = {}
        d['Service_Description'] = servicedesc
        d['Cost'] = cost
        d['Time_to_Complete'] = ttc
        s = json.dumps(d)
        res = seller.make_reply(uuid, reqid, topic, s, session['Client_Key'])
        if res.status_code == 200:
            data = json.loads(res.text)
            status = data['Status']
            flash(u'Success!! ' + status, 'success')
            return redirect(url_for('display'))
        elif res.status_code == 404:
            data = json.loads(res.text)
            error = data['Error']
            flash(u'Error!! ' + error, 'danger')
            return redirect(url_for('display'))


@app.route('/namespace/', methods=['GET', 'POST'])
def namespace():
    file_type = ''
    if request.method == 'GET':
        file_type = 'Photo'
    else:
        file_type = request.form['FileType']
    client_key = session['Client_Key']
    print file_type
    res = seller.list_request(file_type, client_key)
    if res.status_code == 200:
        data = json.loads(res.text)
        print data
        entries = [dict(uuid=row['Uuid'], fileid=row['FileId']) for row in data]
        return render_template("seller-namespace.html", entries=entries, username=session['Name'], file_type=file_type)
    elif res.status_code == 404:
        data = json.loads(res.text)
        error = data['Error']
        flash(u'Error!! ' + error, 'danger')
        return redirect(url_for('display'))


@app.route('/namespace/access/', methods=['POST'])
def namespace_access():
    file_type = request.form['FileType']
    client_key = session['Client_Key']
    res = seller.access_request(file_type, client_key)
    if res.status_code == 200:
        data = json.loads(res.text)
        status = data['Status']
        flash(u'Success!! ' + status, 'success')
        return redirect(url_for('namespace'))
    elif res.status_code == 404:
        data = json.loads(res.text)
        error = data['Error']
        flash(u'Error!! ' + error, 'danger')
        return redirect(url_for('namespace'))


@app.route('/team/')
def team():
    return render_template("team.html")


if __name__ == '__main__':
    app.run(host=seller_server_ip, port=int(seller_server_port), debug=True, threaded=True)

