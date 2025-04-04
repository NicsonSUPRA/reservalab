create table usuario(
    id uuid not null,
    login varchar(255) not null,
    senha varchar(255) not null,
    nome varchar(255) not null,
    client_id uuid,
    authorities varchar[],

    constraint pk_usuario_id primary key (id),
    constraint fk_client_id foreign key (client_id) references client(id)
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
    id bigint not null,
    nome varchar(255) not null,
    data_cadastro timestamp not null,
    data_atualizacao timestamp not null,
    -- usuario_id not null,
    
    constraint pk_laboratorio_id primary key (id),
    -- constraint fk_usuario_id foreign key (usuario_id) references usuario(id)
);
create sequence seq_laboratorio minvalue 1;
ALTER TABLE laboratorio ALTER COLUMN id SET DEFAULT nextval('seq_laboratorio');


create table semestre(
	id bigint not null,
	data_inicio timestamp not null,
	data_fim timestamp not null,

	constraint pk_semestre_id primary key (id)
);

create sequence seq_semestre minvalue 1;
ALTER TABLE semestre ALTER COLUMN id SET DEFAULT nextval('seq_semestre');

--UTIL PARA TRANSAÇÕES DIRETAS NO BANCO DE DADOS DE PRODUÇÃO
begin;
update laboratorio set nome = 'laboratorio ufdpar' where id = 4;

--EXECUTE O BEGGIN E LOGO EM SEGUIDA A SEQUENCIA VERIFIQUE SE A ALTERAÇÃO FOI FEITA CORRETAMENTE
select * from laboratorio

--CASO DE ERRADO EXECUTE O ROWBACK, ELE IRA DESAFZER A ALTERAÇÃO FEITA APÓS O BEGGIN
ROLLBACK;
--CASO ESTEJA TUDO DE ACORDO EXECUTE O COMMIT, ELE IRA SALVAR A ALTERAÇÃO FEITA APÓS O BEGGIN
COMMIT;