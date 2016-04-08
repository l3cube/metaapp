from app import db
db.drop_all()
db.create_all()

from proxy_app import db
db.drop_all()
db.create_all()


