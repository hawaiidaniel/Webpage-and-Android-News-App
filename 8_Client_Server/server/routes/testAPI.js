var express = require('express');
const https = require('https');
var request = require('request');
var router = express.Router();
const bodyParser = require('body-parser');
const app = express();
const cors = require('cors');

app.use(cors());
app.use(bodyParser.urlencoded({ extended: true}));
app.use(bodyParser.json());

var keyword = '';
var html = '';
var url = '';
var nurl = 'https://api.nytimes.com/svc/topstories/v2/home.json?api-key=xd4upDk8zDAqQVbGAnRce0tkmYA2sOgn';
var nurl1 = 'https://api.nytimes.com/svc/topstories/v2/';
var nurl2 = '.json?api-key=xd4upDk8zDAqQVbGAnRce0tkmYA2sOgn';

var gurl = 'https://content.guardianapis.com/search?api-key=385abf6e-f3d2-43ea-bd35-ee85716e05d5&section=(sport|business|technology|politics)&show-blocks=all';
var gurl1 = 'https://content.guardianapis.com/';
var gurl2 = '?api-key=385abf6e-f3d2-43ea-bd35-ee85716e05d5&show-blocks=all';

function getURL(req){
    if(req.query.tabName == "true"){
        if(req.query.secName != "home"){
            req.query.secName = req.query.secName == 'sports' ? 'sport': req.query.secName;
            url = gurl1 + req.query.secName + gurl2;
        } else {
            url = gurl;
        }
    } else {
        if(req.query.secName != "home"){
            url = nurl1 + req.query.secName + nurl2;
        } else {
            url = nurl;
        }
    }
    

    https.get(url, function(res) {
        html='';
        res.on('data',function(data){
            html+=data;
        });
        res.on('end',function(){
            html = JSON.parse(html)
        });
    });
};


router.get('/', function(req, res){

    getURL(req, res);

    var myData = [];
    
    request({
        url:url,
        json: true
    }, function(error, response, body){
        if(!error && response.statusCode == 200){
            if(req.query.tabName == "true"){
                var temp = body['response']['results']
                let count = 0;
                for(var i of temp){
                    if(count >= 10){
                        break;
                    }
                    var myDict = {};
                    myDict.tab = 'true';
                    myDict.title = i['webTitle'];
                    myDict.description = i['blocks']['body'][0]['bodyTextSummary'];
                    myDict.section = i['sectionId'].toUpperCase();
                    myDict.date = i['webPublicationDate'].substring(0,10);
                    myDict.identity = i['id'];
                    myDict.sharedlink = i['webUrl'];
                    try{
                        var imgtemp = i['blocks']['main']['elements'][0]['assets'];
                        var len = imgtemp.length;
                        if(len > 0){
                            if(imgtemp[len-1]['file'] != null || imgtemp[len-1]['file'] != ""){
                                myDict.image = imgtemp[len-1]['file'];
                            }
                            else{
                                myDict.image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                            }
                        }else{
                            myDict.image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                        }
                    } catch(err){
                        myDict.image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                    }
                    myData.push(myDict);
                    count++;
                }
            } else {
                var temp = body['results']
                let count = 0;
                for(var i of temp){
                    if(count >= 10){
                        break;
                    }
                    var myDict = {};
                    myDict.tab = 'false';
                    myDict.title = i['title'];
                    myDict.description = i['abstract'];
                    myDict.section = i['section'].toUpperCase();
                    myDict.date = i['published_date'].substring(0,10);
                    myDict.identity = i['url'];
                    myDict.sharedlink = i['url'];
                    myDict.width = '';
                    myDict.image = '';
                    if(i['multimedia'] != null){
                        for(var j of i['multimedia']){
                            if(j['width'] >= 2000 && j['url'] != ''){
                                myDict.image = j['url'];
                                myDict.width = j['width'];
                                break;
                            }
                        }
                    }
                    if(myDict.image == ''){
                        myDict.image = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
                    }
                    myData.push(myDict);
                    count++;
                }
            }
            
            res.send(myData);
            //res.send(body['results'])
            

        }
    })
    
})

module.exports = router;