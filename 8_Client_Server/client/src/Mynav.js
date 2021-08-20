import React from 'react';
import _ from "lodash";
import { NavLink, Link, withRouter } from 'react-router-dom';
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
import { debounce } from 'lodash';
const axios = require('axios');


class Mynav extends React.Component {
    constructor(props) {
        super(props);
        console.log('kankan')
        console.log(this.props)
        this.state = { checked:localStorage.hasOwnProperty('checked')?JSON.parse(localStorage.getItem('checked')): false, 
            selectedValue: ''};
        this.handleChange = this.handleChange.bind(this);
        this.handleSearchChange = debounce(this.handleSearchChange, 800);
    }

    handleChange(checked){
        localStorage.setItem('checked', checked);
        this.setState({checked}); 
        this.callAPI(checked);
    }


    handleSearchChange = async(value) => {
        try {
            const response = await fetch(
                `https://fei.cognitiveservices.azure.com/bing/v7.0/suggestions?mkt=fr-FR&q=${value}`,
                {
                    headers: {
                        "Ocp-Apim-Subscription-Key": "e9d9f80cd5ce4d7da0dca35e6ed85bf0"
                    }
                }
            );
            const data = await response.json();
            const resultsRaw = data.suggestionGroups[0].searchSuggestions;
            let results = resultsRaw.map(result => ({ label: result.displayText, value: result.url }));
            results.unshift({label:value, value:'myword'})
            results.pop()
            return results;

        } catch (error) {
            console.error(`Error fetching search ${value}`); 
        }
    }



    render(){
        return(
            <rbs.Navbar expand="lg" bg='light' className='myBar' variant="dark">
                <AsyncSelect
                    className='myAsyncSelect'
                    placeholder='Enter Keyword ..'
                    cacheOptions
                    defaultOptions
                    value={this.state.selectedValue}
                    loadOptions={this.handleSearchChange}
                    onChange={(property, value) => {
                        this.setState({ selectedValue: property});
                        this.props.history.push({
                            pathname: '/search',
                            state: {keyword: property.label}
                        })
                    }}
                />

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
                <label htmlFor="material-switch" style={{marginRight:30, marginBottom:0}}>
                    <span>NYTimes</span>
                    <Switch onChange={this.handleChange} checked={this.state.checked} className='react-switch'
                        offColor='#c9cbd1' onColor='#00a2ff' height={28} width={70} uncheckedIcon={false}
                        checkedIcon={false} />
                    <span>Guardian</span>
                </label>
            </rbs.Navbar>


        )
    }
}

export default withRouter(Mynav);