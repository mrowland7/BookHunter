import sys
import webapp2
sys.path.append("../analyze")
import analyze.analyze as alz
import json

class MainPage(webapp2.RequestHandler):
    
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write("Hello world!")

class Info(webapp2.RequestHandler):
    
    def get(self):
        self.response.write("Info page")

    def post(self):
        self.response.write("<!doctype html><html><body><p> Info Request was:")
        num = self.request.get('call_no')
        info = alz.get_info(num, item_map, item_info, lookup_info)
        self.response.write(info)
        self.response.write("</p></body></html>")

class Recommendations(webapp2.RequestHandler):
   
    def get(self):
        self.response.write("Rec page")

    def post(self):
        num = self.request.get('call_no')
        recs = alz.get_recs(num, item_map, item_info, lookup_info)
        print "recs are..." , recs
        self.response.write(json.dumps(recs))
        #for rec in recs:
        #    self.response.write("<p>")
        #    self.response.write(rec)
        #    self.response.write("</p>")

item_map, item_info, lookup_info = alz.init()
print "init finished"

application = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/info', Info),
    ('/recs', Recommendations)
], debug = True)
