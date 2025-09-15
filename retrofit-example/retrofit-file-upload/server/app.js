const express = require('express');
const multer = require('multer');
const path = require('node:path');
const { getPutObjectUrl } = require('./S3Service');

const server = express();

const upload = multer({
    dest: path.resolve(__dirname, 'uploads'),
});

server.post('/single', upload.single('file'), (req,res) => {
    const { filename } = req.file || {};
    console.log('content-type: ', req.headers['content-type']);
    res.json({ filename });
});

server.get('/s3-put-object-url', express.json(), async (req,res) => {
    const { filename, contentType } = req.query;
    console.log(`received filename ${filename} and content-type: ${contentType}`);
    const url = await getPutObjectUrl(filename, contentType);
    console.log('generated presigned s3 put object url ',url);
    res.json({url});
});

server.listen(5000,()=>console.log('server listening at 5000'));