import webapp2
import analyze

class MainPage(webapp2.RequestHandler):
    
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write("Hello world!")

class Info(webapp2.RequestHandler):
    
    def get(self):
        self.response.write("Info page")

    def post(self):
        self.response.write("<!doctype html><html><body><p> Info Request was:")
        content = self.request.get('call_no')
        info = analyze.
        self.response.write(content)
        self.response.write("</p></body></html>")

class Recommendations(webapp2.RequestHandler):
   
    def get(self):
        self.response.write("Rec page")

    def post(self):
        self.response.write("<!doctype html><html><body><p>Rec Request was:")
        content = self.request.get('content')
        self.response.write(content)
        self.response.write("</p></body></html>")


application = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/info', Info),
    ('/recs', Recommendations)
], debug = True)
