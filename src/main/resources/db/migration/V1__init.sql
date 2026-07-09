CREATE TABLE users (
    id uuid NOT NULL,
    cognito_sub varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_cognito_sub UNIQUE (cognito_sub),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'BANNED'))
);

CREATE TABLE projects (
    id uuid NOT NULL,
    name varchar(255) NOT NULL,
    slug varchar(100) NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    deleted_at timestamptz,
    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT uk_projects_slug UNIQUE (slug)
);

CREATE TABLE project_members (
    id uuid NOT NULL,
    project_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role varchar(32) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT pk_project_members PRIMARY KEY (id),
    CONSTRAINT uk_project_members_project_id_user_id UNIQUE (project_id, user_id),
    CONSTRAINT fk_project_members_project_id FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_project_members_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_project_members_role CHECK (role IN ('OWNER', 'MEMBER'))
);

CREATE TABLE api_keys (
    id uuid NOT NULL,
    project_id uuid NOT NULL,
    name varchar(255) NOT NULL,
    key_prefix varchar(64) NOT NULL,
    key_hash varchar(255) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamptz NOT NULL,
    last_used_at timestamptz,
    revoked_at timestamptz,
    CONSTRAINT pk_api_keys PRIMARY KEY (id),
    CONSTRAINT uk_api_keys_key_hash UNIQUE (key_hash),
    CONSTRAINT fk_api_keys_project_id FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT ck_api_keys_status CHECK (status IN ('ACTIVE', 'REVOKED')),
    CONSTRAINT ck_api_keys_revoked_at CHECK (
        (status = 'ACTIVE' AND revoked_at IS NULL)
        OR (status = 'REVOKED' AND revoked_at IS NOT NULL)
    )
);

CREATE TABLE assets (
    id uuid NOT NULL,
    project_id uuid NOT NULL,
    uploaded_by uuid,
    original_file_name varchar(255) NOT NULL,
    content_type varchar(100) NOT NULL,
    size_bytes bigint NOT NULL,
    bucket varchar(63) NOT NULL,
    storage_key varchar(1024) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    deleted_at timestamptz,
    CONSTRAINT pk_assets PRIMARY KEY (id),
    CONSTRAINT uk_assets_storage_key UNIQUE (storage_key),
    CONSTRAINT fk_assets_project_id FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_assets_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users (id),
    CONSTRAINT ck_assets_size_bytes CHECK (size_bytes > 0),
    CONSTRAINT ck_assets_status CHECK (status IN ('UPLOADED', 'PROCESSING', 'READY', 'FAILED'))
);

CREATE TABLE asset_variants (
    id uuid NOT NULL,
    asset_id uuid NOT NULL,
    type varchar(32) NOT NULL,
    content_type varchar(100) NOT NULL,
    size_bytes bigint NOT NULL,
    bucket varchar(63) NOT NULL,
    storage_key varchar(1024) NOT NULL,
    width integer,
    height integer,
    created_at timestamptz NOT NULL,
    CONSTRAINT pk_asset_variants PRIMARY KEY (id),
    CONSTRAINT uk_asset_variants_storage_key UNIQUE (storage_key),
    CONSTRAINT fk_asset_variants_asset_id FOREIGN KEY (asset_id) REFERENCES assets (id),
    CONSTRAINT ck_asset_variants_type CHECK (type IN ('ORIGINAL', 'WEBP', 'THUMBNAIL')),
    CONSTRAINT ck_asset_variants_size_bytes CHECK (size_bytes > 0),
    CONSTRAINT ck_asset_variants_width CHECK (width IS NULL OR width > 0),
    CONSTRAINT ck_asset_variants_height CHECK (height IS NULL OR height > 0)
);

CREATE TABLE processing_logs (
    id uuid NOT NULL,
    asset_id uuid NOT NULL,
    step varchar(32) NOT NULL,
    status varchar(32) NOT NULL,
    message text,
    created_at timestamptz NOT NULL,
    CONSTRAINT pk_processing_logs PRIMARY KEY (id),
    CONSTRAINT fk_processing_logs_asset_id FOREIGN KEY (asset_id) REFERENCES assets (id),
    CONSTRAINT ck_processing_logs_step CHECK (step IN ('UPLOAD', 'WEBP', 'THUMBNAIL', 'COMPLETE')),
    CONSTRAINT ck_processing_logs_status CHECK (status IN ('SUCCESS', 'FAILED'))
);

CREATE INDEX idx_project_members_user_id ON project_members (user_id);
CREATE INDEX idx_api_keys_project_id ON api_keys (project_id);
CREATE INDEX idx_assets_project_id_created_at ON assets (project_id, created_at);
CREATE INDEX idx_asset_variants_asset_id ON asset_variants (asset_id);
CREATE INDEX idx_processing_logs_asset_id ON processing_logs (asset_id);
