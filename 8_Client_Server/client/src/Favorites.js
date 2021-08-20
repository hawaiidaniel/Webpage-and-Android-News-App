import React from 'react';
import { Link } from 'react-router-dom';
import _ from "lodash";
import * as rbs from 'react-bootstrap';
import { FaBookmark } from 'react-icons/fa';
import { MdShare, MdDelete } from 'react-icons/md';
import { EmailShareButton, EmailIcon, FacebookShareButton, FacebookIcon, TwitterShareButton, TwitterIcon } from 'react-share';
import Truncate from 'react-truncate';
import AsyncSelect from 'react-select/async';
import ReactTooltip from 'react-tooltip';
import { ToastContainer, toast, Zoom } from 'react-toastify';
import MediaQuery from 'react-responsive'
import debounce from "debounce-promise";
import 'react-toastify/dist/ReactToastify.css';
import "./custom.css";

class SearchPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = { localStorage: localStorage, checked: localStorage.hasOwnProperty('checked')?JSON.parse(localStorage.getItem('checked')): false,
            results:[], selectedResult: null, show: false, sharedText: '', sharedURL: '', sharedTag: ['CSCI_571_NewsApp'], selectedValue: ''};    
        this.handleShow = this.handleShow.bind(this);
        this.handleClose = this.handleClose.bind(this);
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
    

    deleteMark = (iden) => {
        localStorage.removeItem(iden);
        var temp = localStorage.getItem('myStore');
        temp = JSON.parse(temp);
        var pos = temp.indexOf(iden);
        temp.splice(pos, 1);
        localStorage.setItem('myStore', JSON.stringify(temp));
        this.setState({localStorage});
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

    rmBook = (msg) => toast(
        'Removing ' + msg
    )


    /*componentDidMount() {
        this.callAPI();       
    } */


    render(){

        return (
            <div className = 'SPage' style={{marginBottom:30}}>
                <rbs.Navbar expand="lg" className='myBar' variant="dark">
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
                    <rbs.Nav className="mr-auto">
                        <rbs.Nav.Link href="/">Home</rbs.Nav.Link>
                        <rbs.Nav.Link href="/world">World</rbs.Nav.Link>
                        <rbs.Nav.Link href="/politics">Politics</rbs.Nav.Link>
                        <rbs.Nav.Link href="/business">Business</rbs.Nav.Link>
                        <rbs.Nav.Link href="/technology">Technology</rbs.Nav.Link>
                        <rbs.Nav.Link href="/sports">Sports</rbs.Nav.Link>
                    </rbs.Nav>

                    <Link style={{color: 'inherit', textDecoration: 'inherit', width:24, fontSize:'large', marginRight:30}} to={{pathname: '/favorites'}}>
                        <FaBookmark data-tip="Bookmark" data-place='bottom'  data-effect='solid'/>
                        <ReactTooltip  />
                    </Link>
                    </rbs.Navbar.Collapse>
                </rbs.Navbar>

                <div style={{marginLeft: 15, marginRight: 15}}>
                
                    {localStorage.hasOwnProperty('myStore') && JSON.parse(localStorage.getItem('myStore')).length > 0
                    ?[<rbs.Row><h2 style={{marginLeft:30, marginTop:15}}>Favorites</h2></rbs.Row>,
                        <rbs.Row>{JSON.parse(localStorage.myStore).map((item, i) =>
                            JSON.parse(localStorage.getItem(item))==null 
                                ?null
                                :
                                    <rbs.Col md={3} style={{padding: 0}}>
                                    <rbs.Card style={{marginTop: 15, marginLeft: 15, marginRight: 15}} xs={3}>
                                        <Link style={{color: 'inherit', textDecoration: 'inherit'}} to={{pathname: '/detailed', 
                                                state:{theTab: JSON.parse(localStorage.getItem(item)).tab, theIdentity: item}}}>                    
                                        <rbs.Card.Body>
                                            <rbs.Card.Title style={{marginTop:10, fontSize: 'x-small'}}>
                                                <Truncate lines={2} ellipsis={
                                                    <span>
                                                        ...{" "}
                                                    </span>}>
                                                    {JSON.parse(localStorage.getItem(item)).title}
                                                </Truncate>
                                                {<MdShare onClick={(event) => {
                                                    this.handleShow(event);
                                                    event.stopPropagation();
                                                    event.preventDefault();
                                                    this.setState({sharedText: JSON.parse(localStorage.getItem(item)).title});
                                                    this.setState({sharedURL: JSON.parse(localStorage.getItem(item)).sharedlink}) 
                                                }} />}
                                                <MdDelete onClick={(event) => {
                                                    event.stopPropagation();
                                                    event.preventDefault();
                                                    toast('Removing '+JSON.parse(localStorage.getItem(item)).title);
                                                    this.deleteMark(item);                                                  
                                                    
                                                }}/>
                                                <ToastContainer position='top-center' autoClose={1500} hideProgressBar transition={Zoom} />
                                            </rbs.Card.Title>                      
                                            <rbs.Card.Img src={JSON.parse(localStorage.getItem(item)).image} />
                                            
                                            <rbs.Row style={{marginTop:10, marginBottom: 10, marginLeft: 15, marginRight: 15, fontSize:'x-small'}}>
                                                
                                                <rbs.Col xs={5} style={{padding: 0}}>
                                                    <i>{JSON.parse(localStorage.getItem(item)).date}</i>
                                                </rbs.Col>
                                                
                                                <rbs.Col xs={4} style={{padding: 0}}>
                                                    <div class='float-right'>
                                                    <rbs.Badge style={{backgroundColor:this.sectionColor(JSON.parse(localStorage.getItem(item)).section),
                                                        color:this.textColor(JSON.parse(localStorage.getItem(item)).section)  }} >
                                                        {JSON.parse(localStorage.getItem(item)).section}
                                                    </rbs.Badge>
                                                    </div>
                                                </rbs.Col>
                                                <rbs.Col xs={3} style={{padding: 0}}>
                                                    <div class="float-right">
                                                        <rbs.Badge style={{backgroundColor:this.sectionColor(JSON.parse(localStorage.getItem(item)).source),
                                                            color:this.textColor(JSON.parse(localStorage.getItem(item)).source)  }} >
                                                            {JSON.parse(localStorage.getItem(item)).source}
                                                        </rbs.Badge>
                                                    </div>
                                                </rbs.Col>
                                            </rbs.Row>
                                        </rbs.Card.Body>
                                        </Link>
                                    </rbs.Card>
                                </rbs.Col>
                            )}
                            </rbs.Row>]
                    :<div style={{marginTop:20, textAlign: 'center'}}><h2>You have no saved articles</h2></div>
                    }

                </div>

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
