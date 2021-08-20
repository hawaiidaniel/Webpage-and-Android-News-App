import React from 'react';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import Home from './Home';
import DetailPage from './DetailPage';
import SearchPage from './SearchPage';
import Favorites from './Favorites';


class App extends React.Component {
  /*constructor(props) {
      super(props);
      this.state = { apiResponse: "" };
  }*/
  

  render() {
    return(
      <React.Fragment>
        <Router>
          <Switch>
            <Route exact path='/' component={Home} />            
            <Route path='/world' component={Home} />
            <Route path='/politics' component={Home} />
            <Route path='/business' component={Home} />
            <Route path='/technology' component={Home} />
            <Route path='/sports' component={Home} />
            <Route path='/detailed' component={DetailPage} />
            <Route path='/search' component={SearchPage} />
            <Route path='/favorites' component={Favorites} />
          </Switch>
        </Router>
      </React.Fragment>
    )
  }
}

export default App;
