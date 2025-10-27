const express = require('express');
const jwt = require('jsonwebtoken');
const { randomUUID } = require('node:crypto');

const app = express();
const PORT = 3000;

const SECRET_KEY = '27fa650d2cd050c08f4b048582321ef9';

// for test purposes expiry seconds are kept short
const ACCESS_EXPIRY = 30;
const REFRESH_EXPIRY = 60;

/**
 * {
 *   "USERNAME": {
 *      userId: "USER_ID",
 *      username: "USERNAME",
 *   }
 * }
 */
const USERS = {};
const USERID_USERNAME_MAP = {};


app.use(express.json());

// token generator
function generateToken(sub, username, expiresIn) {
    const payload = { sub, username };
    return jwt.sign(payload, SECRET_KEY, { algorithm: 'HS256' , expiresIn });
}

function verifyToken(token) {
    const payload = jwt.verify(token, SECRET_KEY, { complete: false });
    return USERS[USERID_USERNAME_MAP[payload.sub]];
}

// Middleware to verify JWT
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (token == null) return res.sendStatus(401);

    try {
        req.user = verifyToken(token);
        next();
    }
    catch(error) {
        return res.sendStatus(401);
    }
}

// Login route to generate JWT
app.post('/login', (req, res) => {
    const { username } = req.body;
    const user = USERS[username];
    if (!user) {
        return res.status(401).json({ message: 'User not found' });
    }

    const accessToken = generateToken(user.userId, username, ACCESS_EXPIRY);
    const refreshToken = generateToken(user.userId, username, REFRESH_EXPIRY);
    res.json({ accessToken, refreshToken, expiresIn: ACCESS_EXPIRY, expiresAt: Math.floor(Date.now()/1000) + ACCESS_EXPIRY });
});

// Register a user
app.post('/register', (req, res) => {
    const { username } = req.body;
    if (USERS[username]) {
        return res.status(400).json({ message: 'User already exists' });
    }
    const userId = randomUUID();
    USERS[username] = { userId, username };
    USERID_USERNAME_MAP[userId] = username;
    res.status(201).json({ message: 'User registered successfully' });
});

// Generate new accessToken based on refreshToken
app.post('/refresh', (req, res)=> {
    const { refreshToken } = req.body;
    if (!refreshToken) {
        return res.status(400).json({ message: 'no refresh token' });
    }
    try {
        const user = verifyToken(refreshToken);
        const accessToken = generateToken(user.userId, user.username, ACCESS_EXPIRY);
        res.status(200).json({ accessToken, refreshToken, expiresIn: ACCESS_EXPIRY, expiresAt: Math.floor(Date.now()/1000) + ACCESS_EXPIRY })
    }
    catch(error) {
        res.sendStatus(403);
    }
})

// Protected route
app.get('/profile', authenticateToken, (req, res) => {
    res.json({ message: `Hello ${req.user.username}` });
});

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});