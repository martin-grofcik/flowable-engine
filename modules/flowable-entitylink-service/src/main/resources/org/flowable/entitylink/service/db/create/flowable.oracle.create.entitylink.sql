create table ACT_RU_ENTITYLINK (
    ID_ NVARCHAR2(64),
    REV_ INTEGER,
    CREATE_TIME_ TIMESTAMP(6),
    LINK_TYPE_ NVARCHAR2(255),
    SCOPE_ID_ NVARCHAR2(255),
    SCOPE_TYPE_ NVARCHAR2(255),
    SCOPE_DEFINITION_ID_ NVARCHAR2(255),
    REF_SCOPE_ID_ NVARCHAR2(255),
    REF_SCOPE_TYPE_ NVARCHAR2(255),
    REF_SCOPE_DEFINITION_ID_ NVARCHAR2(255),
    ROOT_SCOPE_ID_ NVARCHAR2(255),
    ROOT_SCOPE_TYPE_ NVARCHAR2(255),
    HIERARCHY_TYPE_ NVARCHAR2(255),
    primary key (ID_)
);

create index ACT_IDX_ENT_LNK_SCOPE on ACT_RU_ENTITYLINK(SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_ENT_LNK_ROOT_SCOPE on ACT_RU_ENTITYLINK(ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_ENT_LNK_SCOPE_DEF on ACT_RU_ENTITYLINK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

insert into ACT_GE_PROPERTY values ('entitylink.schema.version', '6.5.1.4', 1);