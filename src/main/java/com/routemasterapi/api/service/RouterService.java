package com.routemasterapi.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.routemasterapi.api.entity.RouterEntity;
import com.routemasterapi.api.model.RouterIdRequest;
import com.routemasterapi.api.model.RouterRequestBody;
import com.routemasterapi.api.repositories.RouterRepository;

@Service
public class RouterService {
	@Autowired
	private RouterRepository Routerrepository;

	

	public RouterEntity createRouter(RouterRequestBody RouterReqBody) {

		RouterEntity newRouter = new RouterEntity();
		newRouter.setPincode(RouterReqBody.getPincode());
		newRouter.setName(RouterReqBody.getName());
		newRouter.setDescription(RouterReqBody.getDescription());
		newRouter.setTotalDistance(RouterReqBody.getTotalDistance());
		return Routerrepository.save(newRouter);		 
	}

	public RouterEntity updateRouter(RouterRequestBody RouterReqBody) {
		RouterEntity newRouter = new RouterEntity();
		newRouter.setPincode(RouterReqBody.getPincode());
		newRouter.setName(RouterReqBody.getName());
		newRouter.setDescription(RouterReqBody.getDescription());
		newRouter.setTotalDistance(RouterReqBody.getTotalDistance());
		newRouter.setRouteId(RouterReqBody.getRouteId());
		return Routerrepository.save(newRouter);		 
	}

	public Page<RouterEntity> listAllrouterfromdb(int pageNumber, int size) {
	    Pageable pageable = PageRequest.of(pageNumber, size);
	    return Routerrepository.findAll(pageable);
	}
 
	public String deleteRouter(RouterIdRequest RouterIdReq) {
		int routeId= RouterIdReq.getRouteId();
		Routerrepository.deleteById(routeId);
		return "Record Deleted";
	}

	
}
