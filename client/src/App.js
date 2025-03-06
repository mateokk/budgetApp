import './App.css';
import LoginPage from './pages/Login';
import SignupPage from './pages/Signup';
import { BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import ProtectedRoute from './components/ProtectedRoute'
import NavigationBar from './components/NavigationBar';


function App() {
  return (
    <div className="App">
      <Router>
        <NavigationBar/>
        <Routes>
          <Route path='/login' element={<LoginPage/>}/>
          <Route path='/signup' element={<SignupPage/>}/>
          <Route element={<ProtectedRoute/>}>
            <Route path='/dashboard' element={<Dashboard/>}/>
          </Route>
          <Route path='*' element={<Navigate to="/dashboard"/>}/>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
