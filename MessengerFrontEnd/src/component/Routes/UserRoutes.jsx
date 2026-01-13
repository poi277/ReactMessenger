import { BrowserRouter, Route, Routes } from 'react-router-dom';
import UserHomePage from "../User/UserHomePage"
export default function UserRoutes() {
    return(

        <Routes>
            <Route path="homepage" element={<UserHomePage />} />

        </Routes>
    );
}