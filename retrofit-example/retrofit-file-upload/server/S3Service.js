const { S3Client, PutObjectCommand } = require('@aws-sdk/client-s3');
const { getSignedUrl } = require('@aws-sdk/s3-request-presigner');

const s3Client = new  S3Client({
    region: process.env.S3_REGION,
    credentials: {
        accessKeyId: process.env.AWS_ID,
        secretAccessKey: process.env.AWS_SECRET,
    }
});

async function getPutObjectUrl(filename, contentType) {
    const cmd = new PutObjectCommand({
        Bucket: process.env.S3_BUCKET,
        Key: filename,
        ContentType: contentType,
    });
    const url = await getSignedUrl(s3Client, cmd);
    return url;
}

module.exports = {
    getPutObjectUrl,
}