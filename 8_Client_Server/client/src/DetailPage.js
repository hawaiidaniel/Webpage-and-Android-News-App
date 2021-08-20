import React from 'react';
import commentBox from 'commentbox.io';
import { Link } from 'react-router-dom';
import _ from "lodash";
import * as rbs from 'react-bootstrap';
import { EmailShareButton, EmailIcon, FacebookShareButton, FacebookIcon, TwitterShareButton, TwitterIcon } from 'react-share';
import { FaBookmark, FaRegBookmark } from 'react-icons/fa';
import { IoIosArrowUp, IoIosArrowDown } from 'react-icons/io';
import "./custom.css";
import ShowMore from 'react-show-more';
import { css } from "@emotion/core";
import AsyncSelect from 'react-select/async';
import BounceLoader from 'react-spinners/BounceLoader';
import ReactTooltip from 'react-tooltip';
import { ToastContainer, toast, Zoom } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import debounce from "debounce-promise";
const axios = require('axios');

const override = css`
  display: block;
  margin: 0 auto;
  top: 38%;
`;

class DetailPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = { apiResponse: [], checked:localStorage.hasOwnProperty('checked')?JSON.parse(localStorage.getItem('checked')): false,
            sharedTag: ['CSCI_571_NewsApp'], loading: false, selectedValue: ''};
        const wait = 1000; 
        const loadOptions = inputValue => this.handleSearchChange(inputValue);
        this.debouncedLoadOptions = debounce(loadOptions, wait, {leading: true});
    }

    handleSearchChange = async(value) => {
        try {
            const response = await fetch(
                `https://daniel-li.cognitiveservices.azure.com/bing/v7.0/suggestions?mkt=en-US&q=${value}`,
                {
                    headers: {
                        "Ocp-Apim-Subscription-Key": "fa0fae54bbbc463cb6206da03f5fcf41"
                    }
                }
            );
            const data = await response.json();
            const resultsRaw = data.suggestionGroups[0].searchSuggestions;
            let results = resultsRaw.map((result, i) => ({ label: result.displayText, value: result.url }));
            if(results[0].label != value){
                results.unshift({label:value, value:'myword'})
                results.pop()
            }
            return new Promise((resolve, reject) => {
                resolve(results);
            })

        } catch (error) {
            console.error(`Error fetching search ${value}`); 
        }
    }

    callAPI = async() => {
        this.setState({loading: true});
        //await
        await axios.get('https://server-dot-csci571hw8-273902.wl.r.appspot.com/detailEngine',{            
            params:{tabName: this.props.location.state.theTab, identity: this.props.location.state.theIdentity}
        })
        .then(res => {
            const posts = res.data;
            this.setState({ apiResponse: posts, loading: false});
        })
    }

    componentDidMount() {
        this.callAPI(); 
        this.removeCommentBox = commentBox('5724410040287232-proj');
    }

    componentWillUnmount() {
        this.removeCommentBox();
    }



    onBookmark = (iden, article) => {
        if(localStorage.hasOwnProperty(iden)){
            localStorage.removeItem(iden);
            var temp = localStorage.getItem('myStore');
            temp = JSON.parse(temp);
            var pos = temp.indexOf(iden);
            temp.splice(pos, 1);
            localStorage.setItem('myStore', JSON.stringify(temp));
            this.rmBook(article.title);
            document.getElementById('myMark').style.display = 'none';
            document.getElementById('myMarkReg').style.display = 'block';
        } else {
            localStorage.setItem(iden, JSON.stringify(article));
            if(localStorage.hasOwnProperty('myStore')){
                var temp2 = JSON.parse(localStorage.myStore);
                temp2.push(iden);
                localStorage.setItem('myStore', JSON.stringify(temp2));
            } else {
                var myArr = [iden];
                localStorage.setItem('myStore', JSON.stringify(myArr));
            }
            this.addBook(article.title);
            document.getElementById('myMarkReg').style.display = 'none';
            document.getElementById('myMark').style.display = 'block';
        }
                
    }

    addBook = (msg) => toast(
        'Saving ' + msg,
        {toastId: 'adding'}
    )

    rmBook = (msg) => toast(
        'Removing ' + msg,
        {toastId: 'removing'}
    )



    render(){
        return(
            <div className = 'HomePage' style={{marginBottom:30}}>            
                <rbs.Navbar expand="lg" bg='dark' className='myBar' variant="dark">
                    <AsyncSelect
                        className='myAsyncSelect'
                        placeholder='Enter Keyword ..'
                        value={this.state.selectedValue}
                        loadOptions={inputValue => this.debouncedLoadOptions(inputValue)}
                        onChange={(property, value) => {
                            this.setState({ selectedValue: property});
                            this.props.history.push({
                                pathname: '/search',
                                state: {keyword: property.label}
                            })
                        }}
                    />
                    
                    <rbs.Navbar.Toggle aria-controls="responsive-navbar-nav" />
                    <rbs.Navbar.Collapse id="responsive-navbar-nav">
                    <rbs.Nav className="mr-auto" activeKey={this.state.navKey}>
                        <rbs.Nav.Item><rbs.Nav.Link href="/">Home</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="/world">World</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="/politics">Politics</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="/business">Business</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="technology">Technology</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="/sports">Sports</rbs.Nav.Link></rbs.Nav.Item>
                    </rbs.Nav>
                    <rbs.Nav>
                        <rbs.Nav.Item>                        
                            <Link style={{color: 'inherit', textDecoration: 'inherit', width:24, fontSize:'large', marginRight:30}} to={{pathname: '/favorites'}}>
                            <FaRegBookmark data-tip="Bookmark" data-place='bottom'  data-effect='solid' />
                        </Link>
                        </rbs.Nav.Item>
                    </rbs.Nav>
                    </rbs.Navbar.Collapse>
                </rbs.Navbar>

                {this.state.loading ?
                <div
                    className="sweet-loading" 
                    style={{
                        position: 'absolute', left: '50%', top: '50%',
                        transform: 'translate(-50%, -50%)'
                    }}
                    >           
                    <BounceLoader
                        css={override}
                        size={60}
                        color={"blue"}
                        loading={this.state.loading}
                    /><h3>Loading</h3>
                </div>
                :''
                }
                {this.state.apiResponse.map((article, i) =>
                <rbs.Card style={{marginTop: 15, marginLeft: 15, marginRight: 15}}>
                    <rbs.Card.Body>
                        <rbs.Card.Title><h2><i>{article.title}</i></h2></rbs.Card.Title>
                        
                        <rbs.Card.Subtitle style={{marginTop: 10, marginBottom:10}}>
                            <rbs.Row>
                                <rbs.Col xs={5} style={{padding: 0}}>
                                    <h4 style={{marginLeft:10}}><i>{article.date}
                                    </i></h4>
                                </rbs.Col> 
                                <rbs.Col xs={5} style={{padding: 0}}>
                                    <div class="float-right">
                                        <FacebookShareButton data-tip="Facebook"  data-effect='solid' url={article.sharedlink} hashtag='#CSCI_571_NewsApp'>
                                            <FacebookIcon size={30} round={true}/>
                                        </FacebookShareButton>
                                        <TwitterShareButton data-tip="Twitter"  data-effect='solid' url={article.sharedlink} hashtags={this.state.sharedTag}>
                                            <TwitterIcon size={30} round={true}/>
                                        </TwitterShareButton>
                                        <EmailShareButton data-tip="Email"  data-effect='solid' url={article.sharedlink} subject='#CSCI_571_NewsApp'>
                                            <EmailIcon size={30} round={true}/>
                                        </EmailShareButton>
                                    </div>
                                </rbs.Col>
                                
                                <rbs.Col xs={1} style={{padding: 0}}>
                                    <div id='myMark' class="float-right" style={localStorage.hasOwnProperty(this.props.location.state.theIdentity)?{display:'block'}:{display:'none'}}>
                                            <FaBookmark data-tip="Bookmark"  data-effect='solid' style={{color: 'red', fontSize:'x-large'}} 
                                                onClick={() => {
                                                    this.onBookmark(this.props.location.state.theIdentity,article);
                                                    this.rmBook(article.title);
                                                }} />
                                    </div>
                                    <div id='myMarkReg' class="float-right" style={localStorage.hasOwnProperty(this.props.location.state.theIdentity)?{display:'none'}:{display:'block'}}>
                                            <FaRegBookmark data-tip="Bookmark"  data-effect='solid' style={{color: 'red', fontSize:'x-large'}} 
                                                onClick={() => {
                                                    this.onBookmark(this.props.location.state.theIdentity,article)
                                                    this.addBook(article.title);
                                                }} />
                                    </div>
                                    <ToastContainer position='top-center' autoClose={1500} hideProgressBar transition={Zoom} />
                                    <ReactTooltip />
                                </rbs.Col>
                                <rbs.Col xs={1} style={{padding: 0}}></rbs.Col> 
                            </rbs.Row>
                        </rbs.Card.Subtitle>
                        
                        <rbs.Card.Img src={article.image} />
                        <rbs.Card.Text style={{fontSize:'small'}}>
                            <ShowMore
                                lines={3}
                                more={<IoIosArrowDown />}
                                less={<IoIosArrowUp />}
                            >
                                {article.description}
                            </ShowMore>
                        </rbs.Card.Text>
                    </rbs.Card.Body>
                </rbs.Card>
                
                )}
                
                <div className="commentbox" id={this.props.location.state.theIdentity} height={200} style={{marginTop:30, marginBottom:20, marginLeft:15, marginRight:15}} />
            
            </div>
        )   
    }
}

export default DetailPage;
