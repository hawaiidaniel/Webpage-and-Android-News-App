import React from 'react';
import { Link } from 'react-router-dom';
import { FaRegBookmark } from 'react-icons/fa';
import _ from "lodash";
import * as rbs from 'react-bootstrap';
import "./custom.css";
import { css } from "@emotion/core";
import { MdShare } from 'react-icons/md';
import { EmailShareButton, EmailIcon, FacebookShareButton, FacebookIcon, TwitterShareButton, TwitterIcon } from 'react-share';
import BounceLoader from 'react-spinners/BounceLoader';
import ReactTooltip from 'react-tooltip';
import Truncate from 'react-truncate';
import AsyncSelect from 'react-select/async';
import debounce from "debounce-promise";
const axios = require('axios');

const override = css`
  display: block;
  margin: 0 auto;
  top: 38%;
`;

class SearchPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = { apiResponse: [], checked: localStorage.hasOwnProperty('checked')?JSON.parse(localStorage.getItem('checked')): false,
                        show: false, loading: false, sharedText: '', sharedURL: '', sharedTag: ['CSCI_571_NewsApp'], selectedValue: ''};
        this.handleShow = this.handleShow.bind(this);
        this.handleClose = this.handleClose.bind(this);
        const wait = 1000; 
        const loadOptions = inputValue => this.handleSearchChange(inputValue);
        this.debouncedLoadOptions = debounce(loadOptions, wait, {leading: true});
    }

    handleClose(event) {
		this.setState({ show: false });
	}

	handleShow(event) {
		this.setState({ show: true });
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
        await axios.get('https://server-dot-csci571hw8-273902.wl.r.appspot.com/searchEngine',{
            params:{tabName: this.state.checked, qKey: this.props.location.state.keyword}          
        })
        .then(res => {
            const posts = res.data;
            this.setState({ apiResponse: posts, loading: false});
        })
    }

    callNextAPI = async(myValue) => {
        this.setState({loading: true});
        //await
        await axios.get('https://server-dot-csci571hw8-273902.wl.r.appspot.com/searchEngine',{
            params:{tabName: this.state.checked, qKey: myValue}          
        })
        .then(res => {
            const posts = res.data;
            this.setState({ apiResponse: posts, loading: false});
        })
    }


    componentDidMount() {
        this.callAPI(); 
        console.log('tete')
        console.log(this)
    }

    componentWillReceiveProps(nextProps){
        this.callNextAPI(nextProps.location.state.keyword)
    }


    render(){
        return (
            <div className = 'SPage' style={{marginBottom:30}}>
                <rbs.Navbar expand="lg" className='myBar' variant="dark">
                    <AsyncSelect
                        className='myAsyncSelect'
                        placeholder='Enter Keyword ..'
                        defaultInputValue = {this.props.location.state.keyword}
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
                <rbs.Nav className="mr-auto">
                    <rbs.Nav.Link href="/">Home</rbs.Nav.Link>
                    <rbs.Nav.Link href="/world">World</rbs.Nav.Link>
                    <rbs.Nav.Link href="/politics">Politics</rbs.Nav.Link>
                    <rbs.Nav.Link href="/business">Business</rbs.Nav.Link>
                    <rbs.Nav.Link href="/technology">Technology</rbs.Nav.Link>
                    <rbs.Nav.Link href="/sports">Sports</rbs.Nav.Link>
                </rbs.Nav>

                <Link style={{color: 'inherit', textDecoration: 'inherit', width:24, fontSize:'large', marginRight:30}} to={{pathname: '/favorites'}}>
                    <FaRegBookmark data-tip="Bookmark" data-place='bottom'  data-effect='solid'/>
                    <ReactTooltip  />
                </Link>
                </rbs.Navbar.Collapse>
                </rbs.Navbar>
                {this.state.loading
                ? <>
                    <div className="sweet-loading" 
                    style={{
                        position: 'absolute', left: '50%', top: '50%',
                        transform: 'translate(-50%, -50%)'
                    }}>            
                    <BounceLoader
                        css={override}
                        size={60}
                        color={"blue"}
                        loading={this.state.loading}
                    /><h3>Loading</h3>
                    </div>
                </>
                :<>                      
                <rbs.Container fluid>
                    <rbs.Row style={{marginTop: 15, marginLeft: 15}}><h2>Results</h2></rbs.Row>
                    <rbs.Row>
                        {this.state.apiResponse.map((article, i) =>
                            <rbs.Col md={3} style={{padding: 0}}>
                                <rbs.Card style={{marginTop: 15, marginLeft: 15, marginRight: 15}} xs={3}>
                                    <Link style={{color: 'inherit', textDecoration: 'inherit'}} to={{pathname: '/detailed', 
                                            state:{theTab: article.tab, theIdentity: article.identity}}}>
                                        <rbs.Card.Body>
                                            <rbs.Card.Title style={{marginTop:10, fontSize: 'x-small'}}>
                                                <Truncate lines={2} ellipsis={
                                                    <span>
                                                        ...{" "}
                                                    </span>}>
                                                    {article.title}
                                                </Truncate>
                                                {<MdShare onClick={(event) => {
                                                    this.handleShow(event);
                                                    event.stopPropagation();
                                                    event.preventDefault();
                                                    this.setState({sharedText: article.title});
                                                    this.setState({sharedURL: article.sharedlink}) 
                                                }} />}
                                            </rbs.Card.Title>

                                            <rbs.Card.Img src={article.image} />

                                            <rbs.Row style={{marginTop: 10, marginBottom: 10, fontSize: 'x-small'}}>
                                                <rbs.Col xs={5} style={{padding: 0, marginLeft: 15}}>
                                                    <i>{article.date}</i>
                                                </rbs.Col>
                                                <rbs.Col xs={6} style={{padding: 0}}>
                                                    <div class='float-right'>
                                                    <rbs.Badge style={{backgroundColor:this.sectionColor(article.section),
                                                        color:this.textColor(article.section)  }} >
                                                        {article.section}
                                                    </rbs.Badge>
                                                    </div>
                                                </rbs.Col>
                                            </rbs.Row>

                                        </rbs.Card.Body>
                                    </Link>
                                </rbs.Card>
                            </rbs.Col>    
                        )}
                    </rbs.Row>
                </rbs.Container>
                </>
                }

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
            </div>
        )

    }

}

export default SearchPage;
