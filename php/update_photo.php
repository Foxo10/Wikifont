<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

function respond($success, $extra = []) {
    echo json_encode(array_merge(['success' => $success], $extra));
    exit;
}

$mysqli = new mysqli('localhost', 'Xodiez016', '1pUQN3Vut', 'Xodiez016_usuarios');
if ($mysqli->connect_errno) {
    respond(false, ['message' => 'DB connection error']);
}

$name = $_POST['name'] ?? '';
$email = $_POST['email'] ?? '';
$image = $_POST['image'] ?? '';
if (!$name || !$email || !$image) {
    respond(false, ['message' => 'Missing parameters']);
}

$data = base64_decode($image);
if ($data === false) {
    respond(false, ['message' => 'Invalid image']);
}

$safeName = preg_replace('/[^A-Za-z0-9_-]/', '', $name);
$safeEmail = preg_replace('/[^A-Za-z0-9@._-]/', '', $email);
$filename = "uploads/{$safeName}_{$safeEmail}.png";
if (file_put_contents($filename, $data) === false) {
    respond(false, ['message' => 'Failed to save file']);
}

$stmt = $mysqli->prepare('UPDATE users SET photo=? WHERE email=?');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}

$stmt->bind_param('ss', $filename, $email);
if ($stmt->execute()) {
    $base = 'http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/odiez016/WEB/';
    respond(true, ['photo' => $base . $filename]);
}
respond(false, ['message' => 'Update failed']);

?>