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

var url = '';
var html = '';
var nurl1 = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=web_url:("';
var nurl2 = '")&api-key=xd4upDk8zDAqQVbGAnRce0tkmYA2sOgn';

var gurl1 = 'https://content.guardianapis.com/';
var gurl2 = '?api-key=385abf6e-f3d2-43ea-bd35-ee85716e05d5&show-blocks=all';

function getURL(req){
    if(req.query.tabName == "true"){
        url = gurl1 + req.query.identity + gurl2;
    } else {
        url = nurl1 + req.query.identity + nurl2;
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
                var temp = body['response']['content']               
                var myDict = {};
                myDict.tab = 'true';
                myDict.title = temp['webTitle'];
                myDict.description = temp['blocks']['body'][0]['bodyTextSummary'];
                myDict.date = temp['webPublicationDate'].substring(0,10);
                myDict.sharedlink = temp['webUrl'];
                myDict.section = temp['sectionId'].toUpperCase();
                myDict.source = 'GUARDIAN';
                try{
                    var imgtemp = temp['blocks']['main']['elements'][0]['assets'];
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
                }catch(err){
                    myDict.image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                }
                myData.push(myDict);               
            } else {
                var temp = body['response']['docs'];
                for(var i of temp){
                    var myDict = {};
                    myDict.tab = 'false';
                    myDict.title = i['headline']['main'];
                    myDict.description = i['abstract'];
                    myDict.date = i['pub_date'].substring(0,10);
                    myDict.sharedlink = i['web_url'];
                    myDict.section = i['news_desk'].toUpperCase();
                    myDict.source = 'NYTIMES';
                    myDict.width = '';
                    myDict.image = '';
                    if(i['multimedia'] != null){
                        for(var j of i['multimedia']){
                            if(j['width'] >= 2000 && j['url'] != ''){
                                myDict.image = 'https://www.nytimes.com/' + j['url'];
                                myDict.width = j['width'];
                                break;
                            }
                        }
                    }
                    if(myDict.image == ''){
                        myDict.image = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
                    }
                    myData.push(myDict);
                }
            }           
            res.send(myData);
        }
    })
    
})

module.exports = router;