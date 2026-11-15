create database inventi;


CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMINISTRADOR', 'USUARIO')
        NOT NULL DEFAULT 'USUARIO',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SELECT * FROM usuarios;

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

INSERT INTO categorias (nome, descricao) VALUES
('Computador', 'Desktop'),
('Notebook', 'Notebook'),
('Monitor', 'Monitores de video'),
('Impressora', 'Equipamentos de impressao'),
('Perifericos', 'Teclados, mouses, webcams e outros');

SELECT * FROM categorias;


CREATE TABLE setores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

INSERT INTO setores (nome, descricao) VALUES
('Tecnologia da Informacao', 'TI'),
('Recursos Humanos', 'RH'),
('Financeiro', 'Financeiro'),
('Comercial', 'Comercial'),
('Administrativo', 'Administrativo');

SELECT * FROM setores;


CREATE TABLE equipamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,

    patrimonio VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,

    categoria_id INT NOT NULL,
    setor_id INT,

    marca VARCHAR(100),
    modelo VARCHAR(100),
    numero_serie VARCHAR(100),

    status ENUM(
        'ATIVO',
        'INATIVO',
        'EMPRESTADO',
        'DISPONIVEL'
    ) NOT NULL DEFAULT 'DISPONIVEL',

    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipamento_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),

    CONSTRAINT fk_equipamento_setor
        FOREIGN KEY (setor_id)
        REFERENCES setores(id)
);

INSERT INTO equipamentos (
    patrimonio,
    nome,
    categoria_id,
    setor_id,
    marca,
    modelo,
    numero_serie,
    status
)
VALUES (
    'INV-001',
    'Notebook TI 01',
    2,
    1,
    'Dell',
    'Inspiron',
    'DELL123456',
    'DISPONIVEL'
);

SELECT * FROM equipamentos;


CREATE TABLE emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,

    equipamento_id INT NOT NULL,
    usuario_id INT NOT NULL,

    data_emprestimo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_devolucao DATETIME,

    status ENUM('ATIVO', 'DEVOLVIDO')
        NOT NULL DEFAULT 'ATIVO',

    CONSTRAINT fk_emprestimo_equipamento
        FOREIGN KEY (equipamento_id)
        REFERENCES equipamentos(id),

    CONSTRAINT fk_emprestimo_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);

DESCRIBE emprestimos;


CREATE TABLE historico (
    id INT AUTO_INCREMENT PRIMARY KEY,

    equipamento_id INT NOT NULL,
    usuario_id INT NOT NULL,

    data_movimentacao DATETIME
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    tipo_movimentacao VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_historico_equipamento
        FOREIGN KEY (equipamento_id)
        REFERENCES equipamentos(id),

    CONSTRAINT fk_historico_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);

DESCRIBE historico;




-- =====================================================================
-- ETAPA 04 - Script complementar (Cadastro, Usuários e Solicitações)
-- Use este bloco apenas se for rodar a aplicação em MySQL (ver
-- application.properties). No H2 padrão o Hibernate cria as tabelas.
-- =====================================================================

-- 1) Setor do usuário (tela Usuários / Cadastro de usuário)
ALTER TABLE usuarios
    ADD COLUMN setor_id INT NULL,
    ADD CONSTRAINT fk_usuario_setor
        FOREIGN KEY (setor_id) REFERENCES setores(id);

-- 2) Novo status "Em manutenção" para equipamentos (mantém os antigos)
ALTER TABLE equipamentos
    MODIFY status ENUM('ATIVO', 'EM_MANUTENCAO', 'INATIVO', 'EMPRESTADO', 'DISPONIVEL')
        NOT NULL DEFAULT 'ATIVO';

-- 3) Setores e tipos usados nos formulários
INSERT IGNORE INTO setores (nome, descricao) VALUES ('Financeiro', 'Financeiro'), ('Operações', 'Operações');
INSERT IGNORE INTO categorias (nome, descricao) VALUES ('Switch', 'Switches de rede');

-- 4) Solicitações de equipamento/suporte (portal do solicitante)
CREATE TABLE solicitacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,

    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    setor_id INT NOT NULL,

    justificativa VARCHAR(500) NOT NULL,

    urgencia ENUM('BAIXA', 'MEDIA', 'ALTA')
        NOT NULL DEFAULT 'MEDIA',

    status ENUM('PENDENTE', 'APROVADO', 'EM_ATENDIMENTO', 'CONCLUIDO')
        NOT NULL DEFAULT 'PENDENTE',

    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_solicitacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_solicitacao_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_solicitacao_setor
        FOREIGN KEY (setor_id) REFERENCES setores(id)
);

DESCRIBE solicitacoes;

-- 5) Novo status "Recusado" nas solicitações (botão Recusar do administrador)
ALTER TABLE solicitacoes
    MODIFY status ENUM('PENDENTE', 'APROVADO', 'EM_ATENDIMENTO', 'CONCLUIDO', 'RECUSADO')
        NOT NULL DEFAULT 'PENDENTE';


-- Perfis: 'ADMINISTRADOR' vê o menu completo; 'USUARIO' (solicitante) é
-- redirecionado exclusivamente para /solicitacao.
-- Os usuários iniciais (admin@inventi.com / solicitante@inventi.com) são
-- criados pela aplicação na primeira execução, com a senha já em hash.
