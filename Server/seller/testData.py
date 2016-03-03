from datetime import datetime
from seller_server import db
from seller_server import UserRequests
import json

db.drop_all()
db.create_all()
d1 = {}
d1['Service_Description'] = 'Fast Delivery and Cheaper'
d1['Cost'] = '300.50'
d1['Time_to_Complete'] = '2-2-2015'
s1 = json.dumps(d1)
deadline_s = '11-05-2009'
deadline = datetime.strptime(deadline_s, "%d-%m-%Y")
obj = UserRequests('8446992752', '1',deadline, s1)
db.session.add(obj)
obj = UserRequests('8446992752', '2',deadline, s1)
db.session.add(obj)
obj = UserRequests('8446992752', '3',deadline, s1)
db.session.add(obj)
db.session.commit()
