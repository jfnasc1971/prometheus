 select
   d.co_demanda,
   m.no_modulo,
   s.sg_sistema,
   s.no_sistema,
   tsd.de_situacao_demanda,
   d.nu_demanda,
   m.nu_modulo,
   s.nu_sistema,
   fc.ic_ordem_servico_acionamento, -- Acionamento: 
   fc.ic_quantidade,-- Possui Mensuração: 	
   fc.ic_valor_os_informado, -- Valor OS informado: 	
   fc.ic_calculo_quantidade_os, -- Calcula Mensuração OS: 	
   fc.ic_quantidade_os --Informa Mensuração de forma:   
 from
   gct.gcttb023_ordem_servico os
   inner join gct.gcttb021_demanda d on d.nu_demanda=os.nu_demanda
   inner join gct.gcttb039_sistema s on s.nu_sistema = d.nu_sistema
   inner join gct.gcttb040_modulo m on m.nu_modulo = d.nu_modulo
   inner join gct.gcttb008_tipo_situacao_demanda tsd on (
     tsd.nu_situacao_demanda = d.nu_situacao_demanda
   )
   inner join gct.gcttb013_forma_contrato fc on (
     fc.nu_contrato = os.nu_contrato 
     and fc.nu_tipo_grupo = os.nu_tipo_grupo 
     and fc.nu_tipo_servico = os.nu_tipo_servico 
     and fc.nu_tipo_forma = os.nu_tipo_forma  
   )
 where
   d.co_demanda = 'SP202201344'