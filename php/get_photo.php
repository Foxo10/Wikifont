<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

function respond($success, $extra = []) {
    echo json_encode(array_merge(['success' => $success], $extra));
    exit;
}

// CONEXIÓN A LA BBDD
$DB_SERVER   = "localhost";
$DB_USER     = "Xodiez016";
$DB_PASS     = "1pUQN3Vut";
$DB_DATABASE = "Xodiez016_usuarios";
$mysqli = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
if ($mysqli->connect_errno) respond(false, ['message' => 'Database connection error']);

// RECIBIR DATOS
$name  = $_POST['name']  ?? '';
$email = $_POST['email'] ?? '';
if (!$name || !$email) respond(false, ['message' => 'Missing parameters']);

// GENERAR NOMBRE DE ARCHIVO
$clean_name  = preg_replace('/[^a-zA-Z0-9_\-]/', '_', $name);
$clean_email = preg_replace('/[^a-zA-Z0-9_\-]/', '_', $email);
$filepath    = "uploads/" . $clean_name . "_" . $clean_email . ".jpg";

if (file_exists($filepath)) {
    $imageData = base64_encode(file_get_contents($filepath));
    respond(true, ['photo' => $imageData]);
} else {
    respond(false, ['message' => 'Photo not found']);
}
?>
