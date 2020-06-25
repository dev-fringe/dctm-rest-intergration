drop table if exists bulk_upload_req_log;

create table bulk_upload_req_log (cabinet varchar, object_name varchar, format varchar, content_length varchar, req_type varchar, res_href varchar, network_location varchar, username varchar, password varchar, creation_time Date, session_id varchar);

/*insert into bulk_upload_log (cabinet, object_name , format , content_length , req_type , network_location , username , password) values ('Temp', 'test', 'crtext', '23','INIT_DATA', '', '' ,'') */