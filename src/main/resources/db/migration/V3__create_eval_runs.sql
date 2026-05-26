CREATE TABLE eval_runs (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prompt_version     VARCHAR(20) NOT NULL,
    total_cases        INT NOT NULL,
    passed             INT NOT NULL,
    score              DOUBLE PRECISION NOT NULL,
    structural_passed  INT,
    semantic_passed    INT,
    run_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_eval_runs_prompt_version
    ON eval_runs(prompt_version);