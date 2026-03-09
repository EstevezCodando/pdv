-- Dados iniciais para desenvolvimento

-- Usuarios
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES ('Administrador', 'admin@pdv.com', 'admin123', 'ADMIN', true);
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES ('Operador PDV', 'operador@pdv.com', 'op123', 'OPERADOR', true);
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES ('Maria Silva', 'maria@pdv.com', 'maria123', 'OPERADOR', true);

-- Produtos
INSERT INTO produto (nome, descricao, preco, quantidade_estoque, codigo_barras, ativo) VALUES ('Arroz Integral 1kg', 'Arroz integral tipo 1', 8.90, 150, '7891234560001', true);
INSERT INTO produto (nome, descricao, preco, quantidade_estoque, codigo_barras, ativo) VALUES ('Feijao Preto 1kg', 'Feijao preto tipo 1', 7.50, 200, '7891234560002', true);
INSERT INTO produto (nome, descricao, preco, quantidade_estoque, codigo_barras, ativo) VALUES ('Oleo de Soja 900ml', 'Oleo de soja refinado', 5.99, 80, '7891234560003', true);
INSERT INTO produto (nome, descricao, preco, quantidade_estoque, codigo_barras, ativo) VALUES ('Acucar Cristal 1kg', 'Acucar cristal branco', 4.20, 300, '7891234560004', true);
INSERT INTO produto (nome, descricao, preco, quantidade_estoque, codigo_barras, ativo) VALUES ('Cafe Torrado 500g', 'Cafe torrado e moido', 15.90, 60, '7891234560005', true);
