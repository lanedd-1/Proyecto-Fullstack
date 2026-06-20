package com.semestral.venta.config;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.semestral.venta.model.Detalle;
import com.semestral.venta.model.Venta;
import com.semestral.venta.repository.DetalleRepository;
import com.semestral.venta.repository.VentaRepository;

@Configuration
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	@Bean
	CommandLineRunner initDatabase(VentaRepository ventaRepo, DetalleRepository detalleRepo) {
		return args -> {

			detalleRepo.deleteAll();
			ventaRepo.deleteAll();

			Venta v1 = new Venta();
			v1.setFechaV(LocalDateTime.now().minusDays(2));
			v1.setTotal(140.0);
			ventaRepo.save(v1);

			Detalle d1 = new Detalle();
			d1.setCantidad(2);
			d1.setSubTotal(50.0); 
			d1.setProductoId(1L);
			d1.setIdVenta(v1);
			detalleRepo.save(d1);

			Detalle d2 = new Detalle();
			d2.setCantidad(1);
			d2.setSubTotal(90.0);  
			d2.setProductoId(2L);
			d2.setIdVenta(v1);
			detalleRepo.save(d2);

			v1.getDetalles().add(d1);
			v1.getDetalles().add(d2);
			ventaRepo.save(v1);


			Venta v2 = new Venta();
			v2.setFechaV(LocalDateTime.now().minusDays(1));
			v2.setTotal(350.0);
			ventaRepo.save(v2);

			Detalle d3 = new Detalle();
			d3.setCantidad(3);
			d3.setSubTotal(300.0);
			d3.setProductoId(3L);
			d3.setIdVenta(v2);
			detalleRepo.save(d3);

			Detalle d4 = new Detalle();
			d4.setCantidad(1);
			d4.setSubTotal(50.0);
			d4.setProductoId(4L);
			d4.setIdVenta(v2);
			detalleRepo.save(d4);

			v2.getDetalles().add(d3);
			v2.getDetalles().add(d4);
			ventaRepo.save(v2);


			Venta v3 = new Venta();
			v3.setFechaV(LocalDateTime.now());
			v3.setTotal(180.0);
			ventaRepo.save(v3);

			Detalle d5 = new Detalle();
			d5.setCantidad(1);
			d5.setSubTotal(80.0);  
			d5.setProductoId(5L);
			d5.setIdVenta(v3);
			detalleRepo.save(d5);

			Detalle d6 = new Detalle();
			d6.setCantidad(2);
			d6.setSubTotal(100.0);  
			d6.setProductoId(6L);
			d6.setIdVenta(v3);
			detalleRepo.save(d6);

			v3.getDetalles().add(d5);
			v3.getDetalles().add(d6);
			ventaRepo.save(v3);

			log.info("ventas: {}", List.of(v1.getIdVenta(), v2.getIdVenta(), v3.getIdVenta()));
			log.info("detalles: {}", List.of(d1.getIdDetalle(), d2.getIdDetalle(), d3.getIdDetalle(), d4.getIdDetalle(), d5.getIdDetalle(), d6.getIdDetalle()));
		};
	}

}
