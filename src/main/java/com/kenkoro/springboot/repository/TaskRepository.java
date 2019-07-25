package com.kenkoro.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kenkoro.springboot.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	public Optional<Task> findById(Long name);
	public List<Task> findByNameLike(String name);
	public List<Task> findByIdIsNotNullOrderByIdDesc();
	public Page<Task> findAll(Pageable pageable);

}
