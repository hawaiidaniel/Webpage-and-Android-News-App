import React from 'react';
import _ from "lodash";
import { Link } from 'react-router-dom';
import * as rbs from 'react-bootstrap';
import Switch from 'react-switch';
import { FaRegBookmark } from 'react-icons/fa';
import { MdShare } from 'react-icons/md';
import "./custom.css";
import { EmailShareButton, EmailIcon, FacebookShareButton, FacebookIcon, TwitterShareButton, TwitterIcon } from 'react-share';
import Truncate from 'react-truncate';
import { css } from "@emotion/core";
import BounceLoader from 'react-spinners/BounceLoader';
import ReactTooltip from 'react-tooltip';
import AsyncSelect from 'react-select/async';
import debounce from "debounce-promise";
import MediaQuery from 'react-responsive'
const axios = require('axios');

const override = css`
  display: block;
  margin: 0 auto;
  top: 38%;
`;


class Home extends React.Component {
    constructor(props) {
        super(props);
        this.state = { apiResponse: [], checked:localStorage.hasOwnProperty('checked')?JSON.parse(localStorage.getItem('checked')): false, 
            show: false, sharedText: '', sharedURL: '', sharedTag: ['CSCI_571_NewsApp'], loading: false, navKey: '', selectedValue: ''};
        this.handleShow = this.handleShow.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleChange = this.handleChange.bind(this);
        const wait = 1000; 
        const loadOptions = inputValue => this.handleSearchChange(inputValue);
        this.debouncedLoadOptions = debounce(loadOptions, wait, {leading: true});
    
    }

    handleChange(checked){
        localStorage.setItem('checked', checked);
        this.setState({checked}); 
        this.callAPI(checked);
    }


    handleClose(event) {
		this.setState({ show: false });
	}

	handleShow(event) {
		this.setState({ show: true });
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
    

    sectionColor = section => {
        switch(section) {
            case 'WORLD': return '#863dad';
            case 'POLITICS': return '#0b8c6a';
            case 'BUSINESS': return '#1096de';
            case 'TECHNOLOGY': return '#bdbd22';
            case 'SPORT': return '#e8a725';
            case 'SPORTS': return '#e8a725';           
            case 'GUARDIAN': return '#0f1f3d';
            case 'NYTIMES': return '#b3b6bd';
            default: return '#83857d';
        }
    }

    textColor = text => {
        switch(text) {
            case 'WORLD': return 'white';
            case 'POLITICS': return 'white';
            case 'BUSINESS': return 'white';
            case 'TECHNOLOGY': return 'black';
            case 'SPORT': return 'black';
            case 'SPORTS': return 'black';           
            case 'GUARDIAN': return 'white';
            case 'NYTIMES': return 'black';
            default: return 'white';
        }

    }


    callAPI = async() => {
        this.setState({loading: true});
        let pname = this.props.location.pathname;
        this.setState({navKey:pname});
        if (pname == '/') {           
            pname = '/home';
        }
        let checked = localStorage.getItem('checked');
        //await
        await axios.get('https://server-dot-csci571hw8-273902.wl.r.appspot.com/testAPI',{            
            params:{secName: pname.substring(1,), tabName: checked}          
        })
        .then(res => {
            const posts = res.data;
            this.setState({ apiResponse: posts, loading: false});
        })
    }

    componentDidMount() {
        this.callAPI();
    }

    render(){
        return (
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
                        <rbs.Nav.Item><rbs.Nav.Link href="/technology">Technology</rbs.Nav.Link></rbs.Nav.Item>
                        <rbs.Nav.Item><rbs.Nav.Link href="/sports">Sports</rbs.Nav.Link></rbs.Nav.Item>
                    </rbs.Nav>
                    <rbs.Nav style={{marginRight:30}}>
                        <rbs.Nav.Item>                        
                            <Link style={{color: 'inherit', textDecoration: 'inherit', width:24, fontSize:'large', marginRight:30}} to={{pathname: '/favorites'}}>
                            <FaRegBookmark data-tip="Bookmark" data-place='bottom'  data-effect='solid'/>
                            <ReactTooltip  />
                        </Link>
                        </rbs.Nav.Item>
                        <rbs.Nav.Item>NYTimes</rbs.Nav.Item>
                        <rbs.Nav.Item>
                            <Switch onChange={this.handleChange} checked={this.state.checked} className='react-switch'
                                offColor='#c9cbd1' onColor='#00a2ff' height={28} width={70} uncheckedIcon={false}
                                checkedIcon={false} />
                        </rbs.Nav.Item>
                        <rbs.Nav.Item>Guardian</rbs.Nav.Item>
                    </rbs.Nav>
                    </rbs.Navbar.Collapse>
                </rbs.Navbar>


                {this.state.loading 
                ? 
                <div className="sweet-loading" 
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
                :                
                <>
                {this.state.apiResponse.map((article, i) =>                    
                    <rbs.Card style={{marginTop: 30, marginLeft: 15, marginRight: 15}}>
                        <Link style={{color: 'inherit', textDecoration: 'inherit'}} to={{pathname: '/detailed', 
                            state:{theTab: article.tab, theIdentity: article.identity}}}>
                        <rbs.Row style={{marginLeft: 15, marginRight: 15}}>
                            <rbs.Col xs={2.5} style={{padding: 0}}>
                                {/*<rbs.Card.Img variant="middle" src= {article.image} width={280} height={180} rounded />*/}
                                <rbs.Figure style={{marginLeft: 25, marginBottom: 0, marginRight: 25}}>
                                    <rbs.Figure.Image  className="figure-img img-fluid z-depth-1" width={300} height={180} src={article.image} 
                                        style={{marginTop: 25, marginBottom: 25}}/>
                                </rbs.Figure>
                            </rbs.Col>
                            <rbs.Col style={{padding: 0}}>
                                <rbs.Card.Body style={{paddingTop: 15}}>
                                <rbs.Card.Text>
                                    <h3><b><i>{article.title}</i></b>
                                        {<MdShare onClick={(event) => {
                                            this.handleShow(event);
                                            event.stopPropagation();
                                            event.preventDefault();
                                            this.setState({sharedText: article.title});
                                            this.setState({sharedURL: article.sharedlink}) 
                                        }} />}
                                    </h3>
                                    <p style={{fontSize:'small'}}><Truncate lines={3}>{article.description}</Truncate></p>
                                    <rbs.Row style={{marginTop:30}} style={{fontSize:'small'}}>
                                        <rbs.Col xs={6}>
                                            <p style={{fontSize: 'small'}}><b><i>{article.date}</i></b></p>
                                        </rbs.Col>
                                        <rbs.Col xs={6}>
                                            <div class="float-right">
                                                <rbs.Badge style={{fontSize:'small', backgroundColor:this.sectionColor(article.section),
                                                    color:this.textColor(article.section)  }} >
                                                    {article.section}
                                                </rbs.Badge>
                                            </div>
                                        </rbs.Col>
                                    </rbs.Row>
                                </rbs.Card.Text>
                                </rbs.Card.Body>
                            </rbs.Col>
                            
                        </rbs.Row>
                        </Link>
                    </rbs.Card>
                    
                )}

                <rbs.Modal show={this.state.show} onHide={this.handleClose}>
                    <rbs.Modal.Header closeButton>
                        <rbs.Modal.Title>{this.state.sharedText}</rbs.Modal.Title>
                    </rbs.Modal.Header>
                    <rbs.Modal.Body>
                        <rbs.Row style={{marginBottom:10}}><rbs.Col style={{textAlign: 'center'}}><h3>Share via</h3></rbs.Col></rbs.Row>
                        <rbs.Row>
                            <rbs.Col style={{textAlign: 'center'}}>
                                <FacebookShareButton url={this.state.sharedURL} hashtag='#CSCI_571_NewsApp'>
                                    <FacebookIcon size={50} round={true}/>
                                </FacebookShareButton>
                            </rbs.Col>
                            <rbs.Col style={{textAlign: 'center'}}>
                                <TwitterShareButton url={this.state.sharedURL} hashtags={this.state.sharedTag}>
                                    <TwitterIcon size={50} round={true}/>
                                </TwitterShareButton>
                            </rbs.Col>
                            <rbs.Col style={{textAlign: 'center'}}>
                                <EmailShareButton url={this.state.sharedURL} subject='#CSCI_571_NewsApp'>
                                    <EmailIcon size={50} round={true}/>
                                </EmailShareButton>
                            </rbs.Col>
                        </rbs.Row>
                    </rbs.Modal.Body>
                </rbs.Modal>
                </>
                }
            </div>
        )
    }
}

export default Home;
