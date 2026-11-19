-- =====================================================================
-- InvenTI - Script do banco de dados (MySQL / Workbench)
-- Alinhado com as entidades JPA do projeto (usuarios, setores, categorias,
-- equipamentos, solicitacoes) + dados de exemplo para subir a aplicação.
--
-- Logins de exemplo:

--   flavio.silva@inventi.com / admin123    (Administrador)  
--   henrique.espindola@inventi.com / admin123 (Administrador)  
--   eduardo.alcantara@inventi.com  / admin123  (Administrador)
--   solicitante@inventi.com  / user123   (Solicitante)
--   laiane.santos@inventi.com / admin123 (Solicitante) 
-- =====================================================================

CREATE DATABASE IF NOT EXISTS inventi
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventi;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS historico;
DROP TABLE IF EXISTS emprestimos;
DROP TABLE IF EXISTS solicitacoes;
DROP TABLE IF EXISTS equipamentos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS setores;

SET FOREIGN_KEY_CHECKS = 1;


-- ---------------------------------------------------------------------
-- SETORES  (entidade Setor)
-- ---------------------------------------------------------------------
CREATE TABLE setores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO setores (id, nome) VALUES
(1, 'TI'),
(2, 'Financeiro'),
(3, 'RH'),
(4, 'Operações'),
(5, 'Comercial');


-- ---------------------------------------------------------------------
-- CATEGORIAS  (entidade Categoria - "tipo de equipamento")
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO categorias (id, nome) VALUES
(1, 'Notebook'),
(2, 'Impressora'),
(3, 'Switch'),
(4, 'Monitor'),
(5, 'Periférico'),
(6, 'Desktop');


-- ---------------------------------------------------------------------
-- USUARIOS  (entidade Usuario)
-- perfil: ADMINISTRADOR | USUARIO (solicitante)
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMINISTRADOR', 'USUARIO') NOT NULL DEFAULT 'USUARIO',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    setor_id BIGINT NULL,
    data_cadastro DATETIME NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_setor
        FOREIGN KEY (setor_id) REFERENCES setores(id)
) ENGINE=InnoDB;

INSERT INTO usuarios (id, nome, email, senha, perfil, ativo, setor_id, data_cadastro) VALUES
(1, 'Admin', 'admin@inventi.com',
    '120000:B2UhAUD9RmAEiqBHDyuVbQ==:hQ0nYILNQmJFMFpdQpIaJpJ05ZClvH5ItDqBw8nVvYs=',
    'ADMINISTRADOR', TRUE, 1, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(2, 'Ricardo Passos', 'solicitante@inventi.com',
    '120000:RZLeKZla1N8XVt7J9qHG9Q==:p9jkj9Iyy4KX6Ms6UQ4Aalmw71xvhAZmxYkHJE0zRSk=',
    'USUARIO', TRUE, 3, DATE_SUB(NOW(), INTERVAL 45 DAY)),
(3, 'Laiane Santos', 'laiane.santos@inventi.com',
    '120000:Qy7jVVZmFkZj8Rbf4Ogf3A==:7GAs5cmk3GBOGOjUYx/veUQi5J0gLAfraYXpc5cNKvQ=',
    'USUARIO', TRUE, 2, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(4, 'Eduardo Alcantara', 'eduardo.alcantara@inventi.com',
    '120000:ymbCnvPfFgY12baMo0IeOw==:Q5XROtPG59vCHGxUY5nxH1PCQYnnOFjqOoeo7qm//Oo=',
    'ADMINISTRADOR', TRUE, 1, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(5, 'Henrique Espindola', 'henrique.espindola@inventi.com',
    '120000:KxrONd3T7F9UeTINkF3TlQ==:eISkfiSz/GJm9CW+UcRCg2w7TyYT21jgbhHoo+zfVK0=',
    'ADMINISTRADOR', TRUE, 4, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(6, 'Flavio Silva', 'flavio.silva@inventi.com',
    '120000:RlUg3h27TAul8f+ausE89Q==:/yrG/mHy/ZwchyV5TR1Draa7+CuXb6i2EpbQftRkw/c=',
    'ADMINISTRADOR', FALSE, 2, DATE_SUB(NOW(), INTERVAL 15 DAY));


-- ---------------------------------------------------------------------
-- EQUIPAMENTOS  (entidade Equipamento)
-- status: ATIVO | EM_MANUTENCAO | INATIVO
-- ---------------------------------------------------------------------
CREATE TABLE equipamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patrimonio VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    categoria_id BIGINT NOT NULL,
    setor_id BIGINT NULL,
    marca VARCHAR(100),
    modelo VARCHAR(100),
    status ENUM('ATIVO', 'EM_MANUTENCAO', 'INATIVO') NOT NULL DEFAULT 'ATIVO',
    data_cadastro DATETIME NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipamento_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_equipamento_setor
        FOREIGN KEY (setor_id) REFERENCES setores(id)
) ENGINE=InnoDB;

INSERT INTO equipamentos (id, patrimonio, nome, categoria_id, setor_id, marca, modelo, status, data_cadastro) VALUES
(1,  'INV-001', 'Notebook TI 01',          1, 1, 'Dell',     'Inspiron 15',       'ATIVO',         DATE_SUB(NOW(), INTERVAL 50 DAY)),
(2,  'INV-002', 'Notebook Financeiro 01',  1, 2, 'Lenovo',   'ThinkPad E14',      'ATIVO',         DATE_SUB(NOW(), INTERVAL 40 DAY)),
(3,  'INV-003', 'Monitor TI 01',           4, 1, 'LG',       '24MK430H',          'ATIVO',         DATE_SUB(NOW(), INTERVAL 35 DAY)),
(4,  'INV-004', 'Impressora RH',           2, 3, 'HP',       'LaserJet Pro M404', 'EM_MANUTENCAO', DATE_SUB(NOW(), INTERVAL 30 DAY)),
(5,  'INV-005', 'Switch Datacenter',       3, 1, 'Cisco',    'Catalyst 2960',     'ATIVO',         DATE_SUB(NOW(), INTERVAL 25 DAY)),
(6,  'INV-006', 'Desktop Operações 01',    6, 4, 'Dell',     'OptiPlex 3080',     'ATIVO',         DATE_SUB(NOW(), INTERVAL 20 DAY)),
(7,  'INV-007', 'Teclado e Mouse RH',      5, 3, 'Logitech', 'MK270',             'INATIVO',       DATE_SUB(NOW(), INTERVAL 12 DAY)),
(8,  'INV-008', 'Notebook Comercial 01',   1, 5, 'Acer',     'Aspire 5',          'ATIVO',         DATE_SUB(NOW(), INTERVAL 8 DAY)),
(9,  'INV-009', 'Monitor Financeiro 01',   4, 2, 'Samsung',  'S24F350',           'EM_MANUTENCAO', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 'INV-010', 'Impressora Operações',    2, 4, 'Epson',    'L3250',             'ATIVO',         DATE_SUB(NOW(), INTERVAL 1 DAY));


-- ---------------------------------------------------------------------
-- SOLICITACOES  (entidade Solicitacao)
-- urgencia: BAIXA | MEDIA | ALTA
-- status:   PENDENTE | APROVADO | EM_ATENDIMENTO | CONCLUIDO | RECUSADO
-- ---------------------------------------------------------------------
CREATE TABLE solicitacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    setor_id BIGINT NOT NULL,
    justificativa VARCHAR(500) NOT NULL,
    urgencia ENUM('BAIXA', 'MEDIA', 'ALTA') NOT NULL,
    status ENUM('PENDENTE', 'APROVADO', 'EM_ATENDIMENTO', 'CONCLUIDO', 'RECUSADO')
        NOT NULL DEFAULT 'PENDENTE',
    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_solicitacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_solicitacao_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    CONSTRAINT fk_solicitacao_setor
        FOREIGN KEY (setor_id) REFERENCES setores(id)
) ENGINE=InnoDB;

INSERT INTO solicitacoes (id, usuario_id, categoria_id, setor_id, justificativa, urgencia, status, data_solicitacao) VALUES
(1, 2, 1, 3, 'Notebook atual não liga mais; preciso de um novo para as entrevistas.',          'ALTA',  'PENDENTE',       DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3, 4, 2, 'Segundo monitor para conciliação bancária.',                                      'MEDIA', 'APROVADO',       DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 5, 2, 4, 'Impressora do setor está sem peças; necessária para etiquetas de expedição.',     'ALTA',  'EM_ATENDIMENTO', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 2, 5, 3, 'Troca de teclado e mouse com defeito.',                                           'BAIXA', 'CONCLUIDO',      DATE_SUB(NOW(), INTERVAL 12 DAY)),
(5, 3, 6, 2, 'Desktop extra para estagiário do financeiro.',                                    'BAIXA', 'RECUSADO',       DATE_SUB(NOW(), INTERVAL 9 DAY)),
(6, 5, 3, 4, 'Switch adicional para a nova sala de operações.',                                 'MEDIA', 'PENDENTE',       DATE_SUB(NOW(), INTERVAL 2 HOUR));


-- ---------------------------------------------------------------------
-- EMPRESTIMOS e HISTORICO
-- Ainda não usadas pelo código Java (etapas futuras). Mantidas aqui,
-- já compatíveis com as novas tabelas (BIGINT e novos status).
-- ---------------------------------------------------------------------
CREATE TABLE emprestimos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipamento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_emprestimo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_devolucao DATETIME NULL,
    status ENUM('ATIVO', 'DEVOLVIDO') NOT NULL DEFAULT 'ATIVO',

    CONSTRAINT fk_emprestimo_equipamento
        FOREIGN KEY (equipamento_id) REFERENCES equipamentos(id),
    CONSTRAINT fk_emprestimo_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE historico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipamento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimentacao VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_historico_equipamento
        FOREIGN KEY (equipamento_id) REFERENCES equipamentos(id),
    CONSTRAINT fk_historico_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;


-- ---------------------------------------------------------------------
-- Conferência rápida
-- ---------------------------------------------------------------------
SELECT 'usuarios' AS tabela, COUNT(*) AS total FROM usuarios
UNION ALL SELECT 'setores', COUNT(*) FROM setores
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'equipamentos', COUNT(*) FROM equipamentos
UNION ALL SELECT 'solicitacoes', COUNT(*) FROM solicitacoes;
