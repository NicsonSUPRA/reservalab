create table usuario(
    id uuid not null,
    login varchar(255) not null,
    senha varchar(255) not null,
    nome varchar(255) not null,
    authorities varchar[],

    constraint pk_usuario_id primary key (id)
);

create table client(
    id uuid not null,
    client_id varchar(255) not null,
    client_secret varchar(255) not null,
    redirect_uri varchar(255) not null,
    scope varchar(255) not null,

    constraint pk_client_id primary key (id)
);

create table laboratorio(
    id uuid not null,
    nome varchar(255) not null,

    constraint pk_laboratorio_id primary key (id)
);

