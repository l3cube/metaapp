from seller_sdk import init
init()

from seller_server import db
db.drop_all()
db.create_all()