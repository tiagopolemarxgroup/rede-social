package com.api.rest;

import com.api.domain.model.User;
import com.api.domain.repository.UserRepository;
import com.api.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    private Validator validator;
    @Inject
    private UserRepository userRepository;
    @Transactional
    @POST
    public Response createUser(CreateUserRequest request){
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        if(!violations.isEmpty()){
            return  ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        }
        User user = new User(request);
        userRepository.persist(user);
        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @GET
    public Response findAllUsers(){
       List<User> list =   userRepository.listAll();
        return Response.ok(list).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest request){
        User userId = userRepository.findById(id);
        if(userId != null){
            userId.setName(request.getName());
            userId.setAge(request.getAge());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id){
        User user = userRepository.findById(id);
        if(user != null){
            userRepository.deleteById(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
