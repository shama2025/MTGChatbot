//
//  SetModel.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/19/25.
//

import Foundation

struct CardSet: Codable, Identifiable {
    let object: String
    let id: String
    let code: String
    let tcgplayerId: Int?
    let name: String
    let uri: String
    let scryfallUri: String
    let searchUri: String
    let releasedAt: String
    let setType: String
    let cardCount: Int
    let parentSetCode: String?
    let digital: Bool
    let nonfoilOnly: Bool
    let foilOnly: Bool
    let blockCode: String?
    let block: String?
    let iconSvgUri: String
}

enum SetError: Error {
    case invalidURL
    case invalidResponse
    case invalidData
}
