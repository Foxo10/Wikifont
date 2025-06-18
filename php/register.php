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
$password = $_POST['password'] ?? '';
if (!$name || !$email || !$password) {
    respond(false, ['message' => 'Missing parameters']);
}

$hashed = password_hash($password, PASSWORD_DEFAULT);
$stmt = $mysqli->prepare('INSERT INTO users(name,email,password) VALUES(?,?,?)');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}
$stmt->bind_param('sss', $name, $email, $hashed);
if ($stmt->execute()) {
    respond(true);
} else {
    if ($stmt->errno === 1062) {
        respond(false, ['message' => 'User already exists']);
    }
    respond(false, ['message' => 'Insert failed']);
}

?>