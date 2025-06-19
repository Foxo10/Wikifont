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
if (!$email) {
    respond(false, ['message' => 'Missing parameters']);
}

$stmt = $mysqli->prepare('SELECT photo FROM users WHERE email=?');
if (!$stmt) {
    respond(false, ['message' => 'Query error']);
}
$stmt->bind_param('s', $email);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row) {
    $path = $row['photo'];
    if ($path && file_exists($path)) {
        $photo = base64_encode(file_get_contents($path));
        respond(true, ['photo' => $photo]);
    }
}
respond(false, ['message' => 'Photo not found']);
?>