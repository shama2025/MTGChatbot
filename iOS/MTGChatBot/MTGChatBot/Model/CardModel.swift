//
//  CardModel.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/19/25.
//

import Foundation

struct Card: Codable {
    let object: String
    let id: String
    let oracleId: String
    let multiverseIds: [Int]?
    let mtgoId: Int?
    let tcgplayerId: Int?
    let cardmarketId: Int?
    let name: String
    let lang: String
    let releasedAt: String
    let uri: String
    let scryfallUri: String
    let layout: String
    let highresImage: Bool
    let imageStatus: String
    let imageUris: [String: String]?
    let manaCost: String?
    let cmc: Double
    let typeLine: String
    let oracleText: String?
    let power: String?
    let toughness: String?
    let colors: [String]
    let colorIdentity: [String]
    let keywords: [String]
    let legalities: [String: String]
    let games: [String]
    let reserved: Bool
    let gameChanger: Bool
    let foil: Bool
    let nonfoil: Bool
    let finishes: [String]
    let oversized: Bool
    let promo: Bool
    let reprint: Bool
    let variation: Bool
    let setId: String
    let set: String
    let setName: String
    let setType: String
    let setUri: String
    let scryfallSetUri: String
    let rulingsUri: String
    let printsSearchUri: String
    let collectorNumber: String
    let digital: Bool
    let rarity: String
    let cardBackId: String
    let artist: String
    let artistIds: [String]
    let illustrationId: String?
    let borderColor: String
    let frame: String
    let frameEffects: [String]?
    let securityStamp: String?
    let fullArt: Bool
    let textless: Bool
    let booster: Bool
    let storySpotlight: Bool
    let edhrecRank: Int?
    let preview: Preview?
    let prices: [String: String?]
    let relatedUris: [String: String]
    let purchaseUris: [String: String]
}

struct Preview: Codable {
    let source: String
    let sourceUri: String
    let previewedAt: String
}

enum CardError: Error {
    case invalidURL
    case invalidResponse
    case invalidData
}
