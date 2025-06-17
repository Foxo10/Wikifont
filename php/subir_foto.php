<?php
header('Content-Type: application/json');

$upload_dir = __DIR__ . "/fotos/";
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0755, true);
}

if (isset($_FILES['foto'])) {
    $tmp_name = $_FILES['foto']['tmp_name'];
    $name = basename($_FILES['foto']['name']);

    // Para evitar conflictos: añade fecha/hora al nombre del archivo
    $new_name = uniqid() . "_" . $name;
    $target = $upload_dir . $new_name;

    if (move_uploaded_file($tmp_name, $target)) {
        // URL accesible desde fuera
        $url = "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/odiez016/WEB/fotos/" . $new_name;
        echo json_encode(['success' => true, 'url' => $url]);
    } else {
        echo json_encode(['success' => false, 'error' => 'Error al mover el archivo']);
    }
} else {
    echo json_encode(['success' => false, 'error' => 'No se ha recibido ninguna imagen']);
}
?>
