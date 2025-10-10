CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    login VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuario_roles (
    usuario_id UUID NOT NULL,
    roles VARCHAR(100) NOT NULL,
    PRIMARY KEY (usuario_id, roles),
    FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- create table client (
--     id uuid not null,
--     client_id varchar(255) not null,
--     client_secret varchar(255) not null,
--     redirect_uri varchar(255) not null,
--     scope varchar(255) not null,
--     constraint pk_client_id primary key (id)
-- );

-- create table laboratorio (
--     id bigint not null,
--     nome varchar(255) not null,
--     data_cadastro timestamp not null,
--     data_atualizacao timestamp not null,
--     -- usuario_id not null,
--     constraint pk_laboratorio_id primary key (id),
--     -- constraint fk_usuario_id foreign key (usuario_id) references usuario(id)
-- );

-- create sequence seq_laboratorio minvalue 1;

-- ALTER TABLE laboratorio
-- ALTER COLUMN id
-- SET DEFAULT nextval('seq_laboratorio');

create sequence seq_laboratorio start 1 minvalue 1;

create table laboratorio (
    id bigint not null default nextval('seq_laboratorio'),
    nome varchar(255) not null,
    data_cadastro timestamp not null,
    data_atualizacao timestamp not null,
    constraint pk_laboratorio_id primary key (id)
);

-- create table semestre (
--     id bigint not null,
--     data_inicio timestamp not null,
--     data_fim timestamp not null,
--     ano int not null,
--     periodo int not null,
--     descricao varchar(20) not null,
--     constraint pk_semestre_id primary key (id),
--     constraint uq_semestre_ano_periodo unique (ano, periodo)
-- );

-- create sequence seq_semestre start 1 increment 1 minvalue 1;

-- alter table semestre
-- alter column id
-- set default nextval('seq_semestre');
create sequence seq_semestre start 1 increment 1 minvalue 1;

create table semestre (
    id bigint not null default nextval('seq_semestre'),
    data_inicio timestamp not null,
    data_fim timestamp not null,
    ano int not null,
    periodo int not null,
    descricao varchar(20) not null,
    constraint pk_semestre_id primary key (id),
    constraint uq_semestre_ano_periodo unique (ano, periodo)
);

-- create table reserva (
--     id bigint not null,
--     data_inicio timestamp not null,
--     data_fim timestamp not null,
--     status varchar(20) not null,
--     usuario_id uuid not null,
--     laboratorio_id bigint not null,
--     semestre_id bigint not null,
--     constraint pk_reserva_id primary key (id),
--     constraint fk_reserva_usuario foreign key (usuario_id) references usuario (id),
--     constraint fk_reserva_laboratorio foreign key (laboratorio_id) references laboratorio (id),
--     constraint fk_reserva_semestre foreign key (semestre_id) references semestre (id)
-- );

-- create sequence seq_reserva start 1 increment 1 minvalue 1;

-- alter table reserva
-- alter column id
-- set default nextval('seq_reserva');

-- 🔹 Remover a tabela se já existir
DROP TABLE IF EXISTS reserva CASCADE;

-- 🔹 Remover a sequence se já existir
DROP SEQUENCE IF EXISTS seq_reserva CASCADE;

-- 🔹 Criar sequence para IDs
CREATE SEQUENCE seq_reserva START 1 INCREMENT 1 MINVALUE 1;

-- 🔹 Criar tabela reserva
CREATE TABLE reserva (
    id BIGINT NOT NULL DEFAULT nextval('seq_reserva'),
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP,
    status VARCHAR(20),
    tipo VARCHAR(20), -- NORMAL ou FIXA
    dia_semana INT, -- 1=Segunda ... 7=Domingo
    hora_inicio TIME,
    hora_fim TIME,
    ativo BOOLEAN DEFAULT TRUE,
    usuario_id UUID NOT NULL,
    laboratorio_id BIGINT NOT NULL,
    semestre_id BIGINT NOT NULL,
    CONSTRAINT pk_reserva_id PRIMARY KEY (id),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_reserva_laboratorio FOREIGN KEY (laboratorio_id) REFERENCES laboratorio (id),
    CONSTRAINT fk_reserva_semestre FOREIGN KEY (semestre_id) REFERENCES semestre (id)
);

CREATE INDEX idx_reserva_fixa_lab_dia ON reserva_fixa (laboratorio_id, dia_semana);

CREATE INDEX idx_reserva_fixa_usuario ON reserva_fixa (usuario_id);

-- Sequence para PK (Postgres)
CREATE SEQUENCE seq_reserva_fixa_excecao START 1 INCREMENT 1 MINVALUE 1;

-- Tabela de exceções para reservas fixas
CREATE TABLE reserva_fixa_excecao (
    id BIGINT NOT NULL DEFAULT nextval('seq_reserva_fixa_excecao'),
    reserva_fixa_id BIGINT NOT NULL,
    data DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,          -- exemplos: 'CANCELADA', 'BLOQUEADA'
    motivo VARCHAR(1000),
    usuario_id UUID,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_reserva_fixa_excecao PRIMARY KEY (id),
    CONSTRAINT uk_reserva_fixa_data UNIQUE (reserva_fixa_id, data),
    CONSTRAINT fk_reserva_fixa FOREIGN KEY (reserva_fixa_id) REFERENCES reserva (id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_fixa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Índice para buscas por reserva_fixa_id e intervalo de datas (melhora performance)
CREATE INDEX idx_reserva_fixa_excecao_reserva_data ON reserva_fixa_excecao (reserva_fixa_id, data);


--UTIL PARA TRANSAÇÕES DIRETAS NO BANCO DE DADOS DE PRODUÇÃO
begin;

update laboratorio set nome = 'laboratorio ufdpar' where id = 4;

--EXECUTE O BEGGIN E LOGO EM SEGUIDA A SEQUENCIA VERIFIQUE SE A ALTERAÇÃO FOI FEITA CORRETAMENTE
select * from laboratorio

--CASO DE ERRADO EXECUTE O ROWBACK, ELE IRA DESAFZER A ALTERAÇÃO FEITA APÓS O BEGGIN
ROLLBACK;
--CASO ESTEJA TUDO DE ACORDO EXECUTE O COMMIT, ELE IRA SALVAR A ALTERAÇÃO FEITA APÓS O BEGGIN
COMMIT;