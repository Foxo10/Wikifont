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
$password = $_POST['password'] ?? '';
if (!$name || !$password) {
    respond(false, ['message' => 'Missing parameters']);
}

$stmt = $mysqli->prepare('SELECT name,email,password,photo FROM users WHERE name=?');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}
$stmt->bind_param('s', $name);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if ($row && password_verify($password, $row['password'])) {
    $base = 'http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/odiez016/WEB/';
    respond(true, [
        'name' => $row['name'],
        'email' => $row['email'],
        'photo' => $base . $row['photo']
    ]);
} else {
    respond(false, ['message' => 'Invalid credentials']);
}

?>