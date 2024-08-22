package com.dattqdoan.westlakesidehotel.service;

import com.dattqdoan.westlakesidehotel.exception.InternalServerException;
import com.dattqdoan.westlakesidehotel.exception.ResourceNotFoundException;
import com.dattqdoan.westlakesidehotel.model.Room;
import com.dattqdoan.westlakesidehotel.repository.RoomRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService   {
    @Resource
    private RoomRepository roomRepository;
    @Resource
    private FileStorageService fileStorageService;

//    public Room addNewRoom(MultipartFile file, String roomType,
//                           BigDecimal roomPrice, String username)
//            throws SQLException, IOException {
//        Room room = new Room();
//        room.setRoomType(roomType);
//        room.setRoomPrice(roomPrice);
//        if (!file.isEmpty()) {
//            byte[] photoByte = file.getBytes();
//            Blob photoBlob = new SerialBlob(photoByte);
//            room.setPhoto(photoBlob);
//        }
//        return roomRepository.save(room);
//    }
public Room addNewRoom(MultipartFile file, String roomType,
                       BigDecimal roomPrice, String createdBy, String description, String status)
        throws SQLException, IOException {
    Room room = new Room();
    room.setRoomType(roomType);
    room.setRoomPrice(roomPrice);
    room.setCreatedBy(createdBy);
    room.setUpdatedBy(createdBy);
    room.setDescription(description);
    room.setStatus(status);

    if (!file.isEmpty()) {
        byte[] photoByte = file.getBytes();
        Blob photoBlob = new SerialBlob(photoByte);
        room.setPhoto(photoBlob);
    }
    return roomRepository.save(room);
}

    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes, String updatedBy, String description, String status) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (roomType != null) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);
        if (photoBytes != null && photoBytes.length > 0) {
            try {
                room.setPhoto(new SerialBlob(photoBytes));
            } catch (SQLException ex) {
                throw new InternalServerException("Error updating room");
            }
        }
        room.setUpdatedBy(updatedBy);
        if (description != null) room.setDescription(description);
        if (status != null) room.setStatus(status);
        return roomRepository.save(room);
    }


    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isEmpty()) {
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if (photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isPresent()) {
            roomRepository.deleteById(roomId);
        }
    }

//    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes, String username) {
//        Room room = roomRepository.findById(roomId).get();
//        if (roomType != null) room.setRoomType(roomType);
//        if (roomPrice != null) room.setRoomPrice(roomPrice);
//        if (photoBytes != null && photoBytes.length > 0) {
//            try {
//                room.setPhoto(new SerialBlob(photoBytes));
//            } catch (SQLException ex) {
//                throw new InternalServerException("Error updating room");
//            }
//        }
//        return roomRepository.save(room);
//    }

    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }
}
