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
$name        = $_POST['name']  ?? '';
$email       = $_POST['email'] ?? '';
$imageBase64 = $_POST['photo'] ?? '';

if (!$name || !$email || !$imageBase64) respond(false, ['message' => 'Missing parameters']);

// Tamaño de imagen razonable (<2MB)
if (strlen($imageBase64) > 2*1024*1024) respond(false, ['message' => 'Image too large']);

// GENERAR NOMBRE DE ARCHIVO
$clean_name  = preg_replace('/[^a-zA-Z0-9_\-]/', '_', $name);
$clean_email = preg_replace('/[^a-zA-Z0-9_\-]/', '_', $email);
$saveDir     = "uploads/";
if (!is_dir($saveDir)) mkdir($saveDir, 0777, true);

$filename = $saveDir . $clean_name . "_" . $clean_email . ".jpg";

// GUARDAR IMAGEN
$imageData = base64_decode($imageBase64);
if ($imageData === false) respond(false, ['message' => 'Invalid image data']);

if (file_put_contents($filename, $imageData) === false) {
    respond(false, ['message' => 'Failed to write image file']);
}

// ACTUALIZAR LA RUTA EN LA BBDD
$stmt = $mysqli->prepare('UPDATE users SET photo=? WHERE name=? AND email=?');
$stmt->bind_param('sss', $filename, $name, $email);
if ($stmt->execute()) {
    respond(true, ['message' => 'Photo uploaded successfully', 'photo_path' => $filename]);
} else {
    respond(false, ['message' => 'Database update failed']);
}
?>
