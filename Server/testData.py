from proxy_app import db
from proxy_app import UserResponses
import json

d1 = {}
d1['Service_Description'] = 'Fast Delivery'
d1['Cost'] = '200.50'
d1['Time_to_Complete'] = '31-12-2015'
s1 = json.dumps(d1)

obj = UserResponses('8446992752', '1', 'Food', s1, False)
db.session.add(obj)
obj = UserResponses('8446992752', '2', 'Food', s1, False)
db.session.add(obj)
obj = UserResponses('8446992752', '3', 'Food', s1, False)
db.session.add(obj)
db.session.commit()
