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

$email = $_POST['email'] ?? '';
$image = $_POST['image'] ?? '';
if (!$email || !$image) {
    respond(false, ['message' => 'Missing parameters']);
}

$data = base64_decode($image);
if ($data === false) {
    respond(false, ['message' => 'Invalid image']);
}

$stmt = $mysqli->prepare('UPDATE users SET photo=? WHERE email=?');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}
$stmt->bind_param('ss', $data, $email);
if ($stmt->execute()) {
    respond(true, ['photo' => $image])
}
respond(false, ['message' => 'Update failed']);

?>