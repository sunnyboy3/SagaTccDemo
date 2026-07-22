create database if not exists order_db
  default character set utf8mb4
  collate utf8mb4_bin;

create database if not exists wallet_db
  default character set utf8mb4
  collate utf8mb4_bin;

create database if not exists saga_tcc_db
  default character set utf8mb4
  collate utf8mb4_bin;

create table if not exists order_db.demo_order (
  order_id bigint not null auto_increment comment '订单主键',
  user_id bigint not null comment '下单用户标识',
  amount bigint not null comment '订单支付金额，单位为分',
  status varchar(32) not null comment '订单状态：PENDING、PAID 或 CANCELLED',
  create_time datetime(3) not null comment '创建时间',
  update_time datetime(3) not null comment '更新时间',
  primary key (order_id),
  key idx_demo_order_user (user_id),
  key idx_demo_order_status (status, update_time)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_bin comment='SagaTcc 示例订单表';

create table if not exists wallet_db.wallet_account (
  user_id bigint not null comment '用户标识',
  total_amount bigint not null comment '账户总金额，单位为分',
  frozen_amount bigint not null default 0 comment '事务冻结金额，单位为分',
  update_time datetime(3) not null comment '更新时间',
  primary key (user_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_bin comment='SagaTcc 示例钱包账户表';

insert into wallet_db.wallet_account (user_id, total_amount, frozen_amount, update_time)
values (1, 10000, 0, current_timestamp(3))
on duplicate key update
  total_amount = values(total_amount),
  frozen_amount = 0,
  update_time = current_timestamp(3);

